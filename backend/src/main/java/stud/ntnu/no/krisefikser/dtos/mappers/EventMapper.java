package stud.ntnu.no.krisefikser.dtos.mappers;

import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.dtos.map.event.EventResponse;
import stud.ntnu.no.krisefikser.entities.map.Event;
import stud.ntnu.no.krisefikser.dtos.map.event.EventRequest;
import stud.ntnu.no.krisefikser.entities.map.EventType;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.InvalidGeoJsonException;

import java.time.Instant;
import java.time.ZoneId;

/**
 * Service for mapping between {@link Event} entities and their corresponding DTOs.
 * <p>
 *  Converts {@link EventRequest} objects into {@link Event} entities,
 *  and {@link Event} entities into {@link EventResponse} DTOs for API responses.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EventMapper {

  private static final Logger logger = LogManager.getLogger(EventMapper.class);
  private static final ZoneId OSLO = ZoneId.of("Europe/Oslo");
  // 2025-05-08T07:06:19.759+02:00
  private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;


  /**
   * Converts a {@link EventRequest} DTO to a {@link Event} entity.
   *
   * @param request the {@link EventRequest} DTO containing the event data
   * @param eventType the {@link EventType} associated with the event
   * @return the corresponding {@link Event} entity
   * @throws InvalidGeoJsonException if the GeoJSON string is invalid
   */
  public Event toEntity(EventRequest request, EventType eventType) {
    logger.debug("Mapping EventRequest to Event entity for event type '{}'",
        () -> eventType.getName() + " (ID: " + eventType.getId() + ")");

    Event event = new Event()
        .setTitle(request.getTitle())
        .setDescription(request.getDescription())
        .setSeverity(request.getSeverity())
        .setType(eventType)
        .setStatus(request.getStatus())
        .setStartTime(Instant.parse(request.getStartTime()));

    try {
      GeoJsonReader reader = new GeoJsonReader();
      Geometry geometry = reader.read(request.getGeometryGeoJson());
      geometry.setSRID(4326);
      event.setGeometry(geometry);
    } catch (Exception e) {
      logger.error("Invalid GeoJSON format: {}", request.getGeometryGeoJson(), e);
      throw new InvalidGeoJsonException(CustomErrorMessage.GEOJSON_NOT_VALID);
    }

    if (request.getCircleData() != null) {
      event.setCircleData(CircleDataMapper.toEntity(request.getCircleData()));
    }

    logger.info("Mapped EventRequest to Event entity: {}", event.getId());
    return event;
  }

  /**
   * Converts a {@link Event} entity to a {@link EventResponse} DTO.
   *
   * @param event the {@link Event} entity to convert
   * @return the corresponding {@link EventResponse} DTO
   */
  public EventResponse toDto(Event event) {
    logger.debug("Mapping Event entity to Event DTO for event ID {}", event.getId());

    String startIso = event.getStartTime().atZone(OSLO).format(ISO_OFFSET);

    EventResponse response = new EventResponse()
        .setId(event.getId())
        .setTitle(event.getTitle())
        .setDescription(event.getDescription())
        .setSeverity(event.getSeverity())
        .setStatus(event.getStatus())
        .setStartTime(startIso)
        .setEventType(EventTypeMapper.toDto(event.getType()));

    GeoJsonWriter reader = new GeoJsonWriter();
    Geometry geometry = event.getGeometry();
    if (geometry != null) {
      response.setGeometryGeoJson(reader.write(geometry));
    } else {
      logger.warn("Event with ID {} has no geometry data", event.getId());
      response.setGeometryGeoJson(null);
    }

    if (event.getCircleData() != null) {
      response.setCircleData(CircleDataMapper.toDto(event.getCircleData()));
    }

    logger.info("Mapped Event entity to Event DTO: {}", event.getId());
    return response;
  }
}
