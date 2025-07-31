package stud.ntnu.no.krisefikser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionRequest;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.PositionMapper;
import stud.ntnu.no.krisefikser.entities.map.Position;
import stud.ntnu.no.krisefikser.entities.map.PositionType;
import stud.ntnu.no.krisefikser.repository.PositionRepository;
import stud.ntnu.no.krisefikser.repository.PositionTypeRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelterServiceTest {

  @Mock
  private PositionRepository positionRepository;

  @Mock
  private PositionTypeRepository positionTypeRepository;

  @Mock
  private PositionMapper positionMapper;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private ClassPathResource classPathResource;

  @InjectMocks
  private ShelterService shelterService;

  private PositionType shelterType;

  @BeforeEach
  void setUp() {
    // Common setup for tests
    shelterType = new PositionType();
    shelterType.setId(1L);
    shelterType.setName(ShelterService.POSITION_TYPE_SHELTER);


    // Initialize shelters list to empty to start fresh for each test
    ReflectionTestUtils.setField(shelterService, "shelters", new ArrayList<>());
  }


  @Test
  @DisplayName("Should handle missing shelter type")
  void loadSheltersFromGeoJson_WithMissingShelterType_ShouldReturnEmptyList() throws IOException {
    // Arrange
    when(positionTypeRepository.findByName(ShelterService.POSITION_TYPE_SHELTER))
        .thenReturn(Optional.empty());

    // Act - Call loadSheltersFromGeoJson through reflection
    List<PositionResponse> result = ReflectionTestUtils.invokeMethod(shelterService, "loadSheltersFromGeoJson");

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());

    // Verify repository was called
    verify(positionTypeRepository).findByName(ShelterService.POSITION_TYPE_SHELTER);
  }

  @Test
  @DisplayName("Should get all shelters")
  void getAllShelters_ShouldReturnCombinedList() {
    // Arrange
    Position dbShelter1 = new Position();
    dbShelter1.setId(1L);
    dbShelter1.setTitle("DB Shelter 1");
    dbShelter1.setLatitude(59.9127);
    dbShelter1.setLongitude(10.7461);

    Position dbShelter2 = new Position();
    dbShelter2.setId(2L);
    dbShelter2.setTitle("DB Shelter 2");
    dbShelter2.setLatitude(59.9150);
    dbShelter2.setLongitude(10.7500);

    List<Position> dbShelters = Arrays.asList(dbShelter1, dbShelter2);

    PositionResponse dbResponse1 = PositionResponse.builder()
        .title("DB Shelter 1")
        .latitude(59.9127)
        .longitude(10.7461)
        .build();

    PositionResponse dbResponse2 = PositionResponse.builder()
        .title("DB Shelter 2")
        .latitude(59.9150)
        .longitude(10.7500)
        .build();

    PositionResponse geoResponse = PositionResponse.builder()
        .title("GeoJSON Shelter")
        .latitude(59.9200)
        .longitude(10.7600)
        .build();

    // Mock repository and mapper
    when(positionRepository.findAllByPositionTypeName(ShelterService.POSITION_TYPE_SHELTER))
        .thenReturn(dbShelters);
    when(positionMapper.toDto(dbShelter1)).thenReturn(dbResponse1);
    when(positionMapper.toDto(dbShelter2)).thenReturn(dbResponse2);

    // Add GeoJSON shelter to the service's shelter list
    List<PositionResponse> geoShelters = new ArrayList<>();
    geoShelters.add(geoResponse);
    ReflectionTestUtils.setField(shelterService, "shelters", geoShelters);

    // Act
    List<PositionResponse> result = shelterService.getAllShelters();

    // Assert
    assertEquals(3, result.size());
    assertTrue(result.contains(dbResponse1));
    assertTrue(result.contains(dbResponse2));
    assertTrue(result.contains(geoResponse));

    // Verify
    verify(positionRepository).findAllByPositionTypeName(ShelterService.POSITION_TYPE_SHELTER);
    verify(positionMapper).toDto(dbShelter1);
    verify(positionMapper).toDto(dbShelter2);
  }

  @Test
  @DisplayName("Should get shelters by location and radius")
  void getSheltersByLocationAndRadius_ShouldReturnNearbyShelters() {
    // Arrange
    double centerLat = 59.9127;
    double centerLon = 10.7461;
    double radius = 1000; // 1000 meters radius

    // Create DB shelters - one within radius, one outside
    Position dbShelterInside = new Position();
    dbShelterInside.setId(1L);
    dbShelterInside.setTitle("DB Shelter Inside");
    dbShelterInside.setLatitude(59.9130); // Very close to center
    dbShelterInside.setLongitude(10.7465);

    Position dbShelterOutside = new Position();
    dbShelterOutside.setId(2L);
    dbShelterOutside.setTitle("DB Shelter Outside");
    dbShelterOutside.setLatitude(59.9227); // More than 1km away
    dbShelterOutside.setLongitude(10.7661);

    List<Position> dbShelters = Arrays.asList(dbShelterInside, dbShelterOutside);

    PositionResponse dbResponseInside = PositionResponse.builder()
        .title("DB Shelter Inside")
        .latitude(59.9130)
        .longitude(10.7465)
        .build();

    // Create GeoJSON shelters - one within radius, one outside
    PositionResponse geoShelterInside = PositionResponse.builder()
        .title("GeoJSON Shelter Inside")
        .latitude(59.9135)
        .longitude(10.7470)
        .build();

    PositionResponse geoShelterOutside = PositionResponse.builder()
        .title("GeoJSON Shelter Outside")
        .latitude(59.9327)
        .longitude(10.7761)
        .build();

    // Mock repository and mapper
    when(positionRepository.findAllByPositionTypeName(ShelterService.POSITION_TYPE_SHELTER))
        .thenReturn(dbShelters);
    when(positionMapper.toDto(dbShelterInside)).thenReturn(dbResponseInside);

    // Add GeoJSON shelters to the service's shelter list
    List<PositionResponse> geoShelters = new ArrayList<>();
    geoShelters.add(geoShelterInside);
    geoShelters.add(geoShelterOutside);
    ReflectionTestUtils.setField(shelterService, "shelters", geoShelters);

    // Act
    List<PositionResponse> result = shelterService.getSheltersByLocationAndRadius(centerLat, centerLon, radius);

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.contains(dbResponseInside));
    assertTrue(result.contains(geoShelterInside));
    assertFalse(result.stream().anyMatch(s -> "DB Shelter Outside".equals(s.getTitle())));
    assertFalse(result.stream().anyMatch(s -> "GeoJSON Shelter Outside".equals(s.getTitle())));

    // Verify
    verify(positionRepository).findAllByPositionTypeName(ShelterService.POSITION_TYPE_SHELTER);
    verify(positionMapper).toDto(dbShelterInside);
    verify(positionMapper, never()).toDto(dbShelterOutside);
  }

  @Test
  @DisplayName("Should create shelter")
  void createShelter_ShouldReturnCreatedShelter() {
    // Arrange
    PositionRequest request = new PositionRequest();
    request.setTitle("New Shelter");
    request.setDescription("A new test shelter");
    request.setLatitude(59.9127);
    request.setLongitude(10.7461);
    request.setCapacity(150);

    Position newPosition = new Position();
    newPosition.setId(3L);
    newPosition.setTitle("New Shelter");
    newPosition.setDescription("A new test shelter");
    newPosition.setLatitude(59.9127);
    newPosition.setLongitude(10.7461);
    newPosition.setCapacity(150);
    newPosition.setPositionType(shelterType);

    PositionResponse expectedResponse = PositionResponse.builder()
        .id(3L)
        .title("New Shelter")
        .description("A new test shelter")
        .latitude(59.9127)
        .longitude(10.7461)
        .capacity(150)
        .build();

    // Mock repository and mapper
    when(positionMapper.toEntity(request, shelterType)).thenReturn(newPosition);
    when(positionRepository.save(newPosition)).thenReturn(newPosition);
    when(positionMapper.toDto(newPosition)).thenReturn(expectedResponse);

    // Act
    PositionResponse result = shelterService.createShelter(request, shelterType);

    // Assert
    assertEquals(expectedResponse, result);

    // Verify
    verify(positionMapper).toEntity(request, shelterType);
    verify(positionRepository).save(newPosition);
    verify(positionMapper).toDto(newPosition);
  }

  @Test
  @DisplayName("Should calculate distance correctly")
  void calculateDistance_ShouldReturnCorrectDistance() {
    // Arrange
    double lat1 = 59.9127; // Oslo
    double lon1 = 10.7461;
    double lat2 = 59.9139; // A point ~150m away
    double lon2 = 10.7478;

    // Expected distance
    double expectedDistance = 164.0;
    double tolerance = 5.0; // 5 meters tolerance for floating point calculations

    // Act
    double result = ReflectionTestUtils.invokeMethod(shelterService, "calculateDistance",
        lat1, lon1, lat2, lon2);

    // Assert
    assertNotNull(result);
    assertTrue(Math.abs(result - expectedDistance) < tolerance,
        "Distance calculation should be within tolerance. Expected ~" + expectedDistance +
            " meters, but got " + result + " meters");
  }
}