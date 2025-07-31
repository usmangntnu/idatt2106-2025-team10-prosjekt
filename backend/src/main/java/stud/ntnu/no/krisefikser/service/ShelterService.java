package stud.ntnu.no.krisefikser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.proj4j.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionRequest;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.PositionMapper;
import stud.ntnu.no.krisefikser.dtos.mappers.PositionTypeMapper;
import stud.ntnu.no.krisefikser.entities.map.Position;
import stud.ntnu.no.krisefikser.entities.map.PositionType;
import stud.ntnu.no.krisefikser.repository.PositionRepository;
import stud.ntnu.no.krisefikser.repository.PositionTypeRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ShelterService {

  private static final Logger logger = LogManager.getLogger(ShelterService.class);
  public static final String POSITION_TYPE_SHELTER = "Tilfluktsrom";

  private final PositionRepository positionRepository;
  private final PositionTypeRepository positionTypeRepository;
  private final PositionMapper positionMapper;

  private final List<PositionResponse> shelters = new ArrayList<>();

  private static final CoordinateTransform transform;

  /**
   * Static block to initialize the coordinate transformation.
   */
  static {
    CRSFactory crsFactory = new CRSFactory();
    CoordinateReferenceSystem utm33n = crsFactory.createFromParameters(
        "UTM33N", "+proj=utm +zone=33 +ellps=GRS80 +units=m +no_defs");
    CoordinateReferenceSystem wgs84 = crsFactory.createFromParameters(
        "WGS84", "+proj=longlat +datum=WGS84 +no_defs");
    CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    transform = ctFactory.createTransform(utm33n, wgs84);
  }

  /**
   * Method to initialize the service and load shelters from the GeoJSON file.
   *
   * @throws IOException if an error occurs while reading the GeoJSON file
   */
  @PostConstruct
  private void init() throws IOException {
    logger.info("Loading shelters from GeoJSON file");
    shelters.addAll(loadSheltersFromGeoJson());
    logger.info("Loaded {} shelters", shelters.size());
  }

  /**
   * Loads shelters from a GeoJSON file located in the classpath.
   *
   * @return a list of {@link PositionResponse} objects representing the shelters
   * @throws IOException if an error occurs while reading the GeoJSON file
   */
  private List<PositionResponse> loadSheltersFromGeoJson() throws IOException {

    ObjectMapper objectMapper = new ObjectMapper();
    ClassPathResource resource = new ClassPathResource("/tilfluktsrom.geojson");
    JsonNode root = objectMapper.readTree(resource.getInputStream());

    PositionType shelterType = positionTypeRepository.findByName(POSITION_TYPE_SHELTER)
        .orElse(null);
    logger.debug("Shelter type from database: {}", shelterType);

    if (shelterType == null) {
      logger.error("Shelter type not found in database");
      return List.of();
    }

    List<PositionResponse> shelterList = new ArrayList<>();

    for (JsonNode feature : root.get("features")) {
      JsonNode geometry = feature.get("geometry");
      JsonNode properties = feature.get("properties");

      if (geometry == null || properties == null) {
        logger.warn("Skipping invalid feature: {}", feature);
        continue;
      }

      double x = geometry.get("coordinates").get(0).asDouble();
      double y = geometry.get("coordinates").get(1).asDouble();

      ProjCoordinate utmCoord = new ProjCoordinate(x, y);
      ProjCoordinate wgsCoord = new ProjCoordinate();
      transform.transform(utmCoord, wgsCoord);

      logger.debug("Transformed coordinates: UTM({},{}) -> WGS({},{})", x, y, wgsCoord.x, wgsCoord.y);

      PositionResponse shelter = PositionResponse.builder()
          .title(properties.path("navn").asText("Tilfluktsrom"))
          .description(properties.path("beskrivelse").asText(null))
          .capacity(properties.path("cap").isInt() ? properties.path("cap").asInt() : null)
          .latitude(wgsCoord.y)
          .longitude(wgsCoord.x)
          .type(PositionTypeMapper.toDto(shelterType))
          .build();

      logger.debug("Parsed shelter from GeoJSON: {}", shelter);
      shelterList.add(shelter);
    }

    return shelterList;
  }

  /**
   * Fetches all shelters from the database and GeoJSON file.
   *
   * @return a list of {@link PositionResponse} objects representing all shelters
   */
  public List<PositionResponse> getAllShelters() {
    logger.info("Fetching all shelters from DB and GeoJSON");

    List<PositionResponse> combined = new ArrayList<>();

    List<Position> dbShelters = positionRepository.findAllByPositionTypeName(POSITION_TYPE_SHELTER);
    for (Position pos : dbShelters) {
      PositionResponse dto = positionMapper.toDto(pos);
      logger.debug("DB shelter: {}", dto);
      combined.add(dto);
    }

    for (PositionResponse geo : shelters) {
      logger.debug("GeoJSON shelter: {}", geo);
      combined.add(geo);
    }

    logger.info("Total shelters: {}", combined.size());
    return combined;
  }

  /**
   * Fetches shelters within a specified radius of a given location.
   *
   * @param latitude the latitude of the location
   * @param longitude the longitude of the location
   * @param radius the radius in meters
   * @return a list of {@link PositionResponse} objects representing shelters within the radius
   */
  public List<PositionResponse> getSheltersByLocationAndRadius(double latitude, double longitude, double radius) {
    logger.info("Searching for shelters within {} meters of ({}, {})",
        radius, latitude, longitude);

    List<PositionResponse> nearby = new ArrayList<>();

    List<Position> dbShelters = positionRepository.findAllByPositionTypeName(POSITION_TYPE_SHELTER);
    for (Position pos : dbShelters) {
      double distance = calculateDistance(latitude, longitude, pos.getLatitude(), pos.getLongitude());
      if (distance <= radius) {
        PositionResponse dto = positionMapper.toDto(pos);
        logger.debug("Found DB shelter within radius: {}", dto);
        nearby.add(dto);
      }
    }

    for (PositionResponse geoShelter : shelters) {
      double distance = calculateDistance(latitude, longitude, geoShelter.getLatitude(),
          geoShelter.getLongitude());
      if (distance <= radius) {
        logger.debug("Found GeoJSON shelter within radius: {}", geoShelter);
        nearby.add(geoShelter);
      }
    }

    logger.info("Found {} shelters within {} meters", nearby.size(), radius);
    return nearby;
  }

  /**
   * Creates a new shelter and saves it to the database.
   *
   * @param request the request containing shelter details
   * @param type the type of the position (should be "Shelter")
   * @return a {@link PositionResponse} object representing the created shelter
   */
  public PositionResponse createShelter(PositionRequest request, PositionType type) {
    Position position = positionMapper.toEntity(request, type);
    position = positionRepository.save(position);
    logger.info("Shelter created and saved with ID: {}", position.getId());
    return positionMapper.toDto(position);
  }

  /**
   * Calculates the distance between two geographical points using the Haversine formula.
   *
   * @param lat1 the latitude of the first point
   * @param lon1 the longitude of the first point
   * @param lat2 the latitude of the second point
   * @param lon2 the longitude of the second point
   * @return the distance in meters
   */
  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371000;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }
}
