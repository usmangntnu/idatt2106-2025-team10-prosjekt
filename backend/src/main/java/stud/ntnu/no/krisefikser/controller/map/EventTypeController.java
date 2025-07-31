package stud.ntnu.no.krisefikser.controller.map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.krisefikser.service.EventTypeService;
import stud.ntnu.no.krisefikser.dtos.map.event.EventTypeResponse;

import java.util.List;

/**
 * Controller class for handling requests related to event types.
 * <p>
 * This class provides endpoints for retrieving event types from the database.
 * </p>
 */
@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
@Tag(name = "EventTypes", description = "Endpoints for retrieving event types")
public class EventTypeController {

  private static final Logger logger = LogManager.getLogger(EventController.class);

  private final EventTypeService eventTypeService;

  /**
   * Method to retrieve all event types.
   *
   * @return a response containing a list of all {@link EventTypeResponse} objects.
   */
  @Operation(summary = "Gets all Event Types",
      description = "Fetches all event types from the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event Types fetched successfully"),
      @ApiResponse(responseCode = "404", description = "No Event Types found in the database")
  })
  @GetMapping
  public ResponseEntity<List<EventTypeResponse>> getAllEventTypes() {
    logger.info("Fetching all event types");
    List<EventTypeResponse> eventTypes = eventTypeService.getAllEventTypes();
    logger.info("Retrieved {} event types", eventTypes.size());
    return ResponseEntity.ok(eventTypes);
  }

  /**
   * Method to retrieve an event type by its ID.
   *
   * @param id the ID of the event type to retrieve
   * @return a response containing the {@link EventTypeResponse} object for the specified event type
   */
  @Operation(summary = "Get event type by ID", description = "Fetches an event type by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event Type fetched successfully"),
      @ApiResponse(responseCode = "404", description = "Event Type not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<EventTypeResponse> getEventTypeById(
      @Parameter(description = "ID of the event type to retrieve", required = true)
      @PathVariable Long id) {
    logger.info("Fetching event type with ID: {}", id);
    EventTypeResponse eventType = eventTypeService.getEventTypeById(id);
    logger.info("Retrieved event type: {}", eventType);
    return ResponseEntity.ok(eventType);
  }
}
