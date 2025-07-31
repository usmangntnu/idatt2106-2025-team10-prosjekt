package stud.ntnu.no.krisefikser.controller.map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionRequest;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionResponse;
import stud.ntnu.no.krisefikser.entities.map.PositionType;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.PositionTypeRepository;
import stud.ntnu.no.krisefikser.service.PositionService;
import stud.ntnu.no.krisefikser.service.ShelterService;

import java.util.List;

/**
 * Controller for managing positions in the system.
 *<p>
 *   This class is responsible for handling requests related to positions,
 *   such as retrieving all positions,
 *   creating new positions, and updating existing ones.
 *</p>
 */
@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
@Tag(name = "Positions", description = "Endpoints for managing positions in the system")
public class PositionController {

  private static final Logger logger = LogManager.getLogger(PositionController.class);
  private final ShelterService shelterService;
  private final PositionService positionService;
  private final PositionTypeRepository positionTypeRepository;
  private final SimpMessagingTemplate messagingTemplate;


  /**
   * Method for retrieving all positions.
   *
   * @return a response entity containing a list of all positions
   */
  @Operation(summary = "Gets all positions",
      description = "Fetches all positions from the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Positions fetched successfully"),
      @ApiResponse(responseCode = "404", description = "No positions found in the database")
  })
  @GetMapping
  public ResponseEntity<List<PositionResponse>> getAllPositions() {
    logger.info("Fetching all positions");
    List<PositionResponse> positions = positionService.getAllPositions();
    logger.info("Retrieved {} positions", positions.size());
    return ResponseEntity.ok(positions);
  }

  /**
   * Gets a position from the database by its ID.
   *
   * @param id the ID of the position to retrieve
   * @return a response entity containing the position
   */
  @Operation(summary = " Get position by ID",
      description = "Fetches a position from the database by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Position fetched successfully"),
      @ApiResponse(responseCode = "404", description = "Position not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<PositionResponse> getPositionById(
      @PathVariable long id) {
    logger.info("Fetching position with ID: {}", id);
    PositionResponse position = positionService.getPositionById(id);
    logger.info("Retrieved position with ID: {}", position.getId());
    return ResponseEntity.ok(position);
  }

  /**
   * Add a new position to the database.
   *
   * @param positionRequest the request body containing position details
   * @return a response entity containing the created position
   */
  @Operation(summary = "Add a new position",
      description = "Adds a new position to the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Position added successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid position details provided")
  })
  @PostMapping
  public ResponseEntity<PositionResponse> createNewPosition(@RequestBody
                                                            PositionRequest positionRequest) {
    logger.info("Creating new position with title '{}'", positionRequest.getTitle());

    logger.info("longitude: {}, latitude: {}",
        positionRequest.getLongitude(), positionRequest.getLatitude());

    PositionType type = positionTypeRepository.findById(positionRequest.getTypeId())
        .orElseThrow(() -> new AppEntityNotFoundException(
            CustomErrorMessage.POSITION_TYPE_NOT_FOUND));

    if (type.getName().equalsIgnoreCase("Shelter")) {
      logger.info("Adding new shelter with title '{}'", positionRequest.getTitle());
      PositionResponse shelter = shelterService.createShelter(positionRequest, type);
      logger.info("Shelter added successfully with ID '{}'", shelter.getId());
      return ResponseEntity.status(201).body(shelter);
    }

    logger.info("Adding new position with title '{}'", positionRequest.getTitle());
    PositionResponse position = positionService.createNewPosition(positionRequest);
    logger.info("Position added successfully with ID '{}'", position.getId());
    messagingTemplate.convertAndSend("/topic/positions", position);
    return ResponseEntity.status(201).body(position);
  }

  /**
   * Method to delete a position from the database.
   *
   * @param id the ID of the position to delete
   * @return a response entity indicating the result of the deletion
   */
  @Operation(summary = "Delete a position",
      description = "Deletes a position from the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Position deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Position not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePosition(@PathVariable long id) {
    logger.info("Deleting position with ID: {}", id);
    positionService.deletePosition(id);
    logger.info("Position with ID: {} deleted successfully", id);
    messagingTemplate.convertAndSend("/topic/positions/delete", id);
    return ResponseEntity.ok().build();
  }

  /**
   * Update an existing position in the database.
   *
   * @param id the ID of the position to update
   * @param positionRequest the request body containing updated position details
   * @return a response entity containing the updated position
   */
  @Operation(summary = "Update an existing position",
      description = "Updates an existing position in the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Position updated successfully"),
      @ApiResponse(responseCode = "404", description = "Position not found")
  })
  @PutMapping("/{id}")
  public ResponseEntity<PositionResponse> updatePosition(
      @PathVariable long id,
      @RequestBody PositionRequest positionRequest) {
    logger.info("Updating position with ID: {}", id);
    PositionResponse updatedPosition = positionService.updatePosition(id, positionRequest);
    logger.info("Position with ID: {} updated successfully", updatedPosition.getId());
    messagingTemplate.convertAndSend("/topic/positions", updatedPosition);
    return ResponseEntity.ok(updatedPosition);
  }

  /**
   * Method to retrieve all shelters from the backend, both from the database and the geojson file.
   *
   * @return a {@link ResponseEntity} containing a list of all shelters
   */
  @Operation(summary = "Gets all shelters",
      description = "Fetches all shelters from the database and geojson file.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Shelters fetched successfully"),
      @ApiResponse(responseCode = "404", description = "No shelters found")
  })
  @GetMapping("/shelters")
  public ResponseEntity<List<PositionResponse>> getAllShelters() {
    logger.info("Fetching all shelters");

    List<PositionResponse> shelters = shelterService.getAllShelters();

    if (shelters.isEmpty()) {
      logger.warn("No shelters found");
      return ResponseEntity.status(404).build();
    }
    logger.info("Retrieved {} shelters", shelters.size());
    return ResponseEntity.ok(shelters);
  }

  @Operation(summary = "Get shelters by location and radius",
      description = "Fetches shelters (from DB and GeoJSON) within a specified radius of a location.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Shelters fetched successfully"),
      @ApiResponse(responseCode = "404", description = "No shelters found in given radius")
  })
  @PostMapping("/shelters-nearby")
  public ResponseEntity<List<PositionResponse>> getSheltersByLocationAndRadius(
      @RequestParam Double latitude,
      @RequestParam Double longitude,
      @RequestParam Double radius) {

    logger.info("Fetching shelters within {} meters of location ({}, {})", radius, latitude, longitude);
    List<PositionResponse> shelters = shelterService.getSheltersByLocationAndRadius(latitude, longitude, radius);

    if (shelters.isEmpty()) {
      logger.warn("No shelters found in radius {} around ({}, {})", radius, latitude, longitude);
      return ResponseEntity.status(404).build();
    }

    logger.info("Found {} shelters", shelters.size());
    return ResponseEntity.ok(shelters);
  }
}
