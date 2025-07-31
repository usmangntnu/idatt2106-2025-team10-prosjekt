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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.krisefikser.dtos.map.event.EventRequest;
import stud.ntnu.no.krisefikser.entities.map.EventType;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.EventTypeRepository;
import stud.ntnu.no.krisefikser.service.EventService;
import stud.ntnu.no.krisefikser.dtos.map.event.EventResponse;

import java.util.List;

/**
 * Controller for handling event-related operations.
 *
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Endpoints for retrieving events and listings filtered by event")
public class EventController {

  private static final Logger logger = LogManager.getLogger(EventController.class);

  private final EventService eventService;
  private final EventTypeRepository eventTypeRepository;
  private final SimpMessagingTemplate messagingTemplate;


  /**
   * Method to retrieve all events.
   *
   * @return a response containing a list of all {@link EventResponse} objects.
   */
  @Operation(summary = "Gets all Events", description = "Fetches all events from the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Events fetched successfully"),
      @ApiResponse(responseCode = "404", description = "No Events found in the database")
  })
  @GetMapping
  public ResponseEntity<List<EventResponse>> getAllEvents() {
    logger.info("Fetching all events");
    List<EventResponse> events = eventService.getAllEvents();
    logger.info("Retrieved {} events", events.size());
    return ResponseEntity.ok(events);
  }

  /**
   * Method to retrieve an event by its ID.
   *
   * @param id the ID of the event to retrieve
   * @return a response containing the {@link EventResponse} object for the specified event
   */
  @Operation(summary = "Get event by ID", description = "Fetches an event by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event fetched successfully"),
      @ApiResponse(responseCode = "404", description = "Event not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<EventResponse> getEventById(
      @Parameter(description = "ID of the event to retrieve", example = "1")
      @PathVariable long id) {
    logger.info("Fetching event with ID: {}", id);
    EventResponse event = eventService.getEventById(id);
    logger.info("Retrieved event with ID: {}", id);
    return ResponseEntity.ok(event);
  }

  /**
   * Method to update an event and its details.
   *
   * @param id the ID of the event to update
   * @param eventRequest the request body containing updated event details
   * @return a response entity containing the updated event
   */
  @Operation(summary = "Update an event", description = "Updates an existing event and its details.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event updated successfully"),
      @ApiResponse(responseCode = "404", description = "Event not found")
  })
  @PutMapping("/{id}")
  public ResponseEntity<EventResponse> updateEvent(
      @Parameter(description = "ID of the event to update", example = "1")
      @PathVariable long id,
      @RequestBody EventRequest eventRequest) {
    logger.info("Updating event with ID: {}", id);
    EventResponse updatedEvent = eventService.updateEvent(id, eventRequest);
    logger.info("Updated event with ID: {}", id);
    messagingTemplate.convertAndSend("/topic/events", updatedEvent);
    return ResponseEntity.ok(updatedEvent);
  }


  /**
   * Method to create a new event and save it to the database.
   *
   * @param eventRequest the request body containing event details
   * @return a response entity containing the created event
   */
  @Operation(summary = "Create a new event", description = "Creates a new event and saves it to " +
      "the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Event created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid event details provided")
  })
  @PostMapping
  public ResponseEntity<EventResponse> createNewEvent(
      @RequestBody EventRequest eventRequest) {
    logger.info("Creating new event with title '{}'", eventRequest.getTitle());

    logger.info("the request has the following details: \n" +
        "description: {},\n" +
        "severity: {},\n" +
        "geometryString: {},\n" +
        "typeId: {},\n" +
        "startTime: {},\n" +
        "status: {}",
        eventRequest.getDescription(),
        eventRequest.getSeverity(),
        eventRequest.getGeometryGeoJson(),
        eventRequest.getTypeId(),
        eventRequest.getStartTime(),
        eventRequest.getStatus());


    EventType eventType = eventTypeRepository.findById(eventRequest.getTypeId())
        .orElseThrow(() -> new AppEntityNotFoundException(
            CustomErrorMessage.EVENT_TYPE_NOT_FOUND));
    logger.info("Event type found: {}", eventType.getName());

    EventResponse event = eventService.createEvent(eventRequest, eventType);
    messagingTemplate.convertAndSend("/topic/events", event);
    logger.info("Event created successfully with ID '{}'", event.getId());

    return ResponseEntity.status(201).body(event);
  }


  /**
   * Retrieve events by event type.
   *
   * @param eventTypeID the ID of the event type to filter events by
   * @return a response containing a list of {@link EventResponse} objects filtered by the
   * specified event type
   */
  @Operation(summary = "Get events by event type", description = "Fetches events filtered by " +
      "the specified event type.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Events fetched successfully"),
      @ApiResponse(responseCode = "404", description = "No events found for the specified event type")
  })
  @GetMapping("/eventType/{eventTypeID}")
  public ResponseEntity<List<EventResponse>> getEventsByEventType(
      @Parameter(description = "ID of the event type to filter events by", example = "1")
      @PathVariable long eventTypeID) {
    logger.info("Fetching events for event type ID: {}", eventTypeID);
    List<EventResponse> events = eventService.getEventsByEventType(eventTypeID);

    logger.info("Retrieved {} events for event type ID: {}", events.size(), eventTypeID);
    return ResponseEntity.ok(events);
  }

  /**
   * Delete an event by its ID.
   *
   * @param id the ID of the event to delete
   * @return a response entity indicating the result of the deletion
   */
  @Operation(summary = "Delete an event", description = "Deletes an event by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Event not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEvent(
      @Parameter(description = "ID of the event to delete", example = "1")
      @PathVariable long id) {
    logger.info("Deleting event with ID: {}", id);
    eventService.deleteEvent(id);
    logger.info("Event deleted successfully with ID: {}", id);
    messagingTemplate.convertAndSend("/topic/events/delete", id);
    return ResponseEntity.ok().build();
  }
}
