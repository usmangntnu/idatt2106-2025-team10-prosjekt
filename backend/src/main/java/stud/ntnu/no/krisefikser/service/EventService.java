package stud.ntnu.no.krisefikser.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.dtos.map.event.EventRequest;
import stud.ntnu.no.krisefikser.dtos.map.event.EventResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.CircleDataMapper;
import stud.ntnu.no.krisefikser.dtos.mappers.EventMapper;
import stud.ntnu.no.krisefikser.entities.map.Event;
import stud.ntnu.no.krisefikser.entities.map.EventType;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.InvalidGeoJsonException;
import stud.ntnu.no.krisefikser.repository.EventRepository;
import stud.ntnu.no.krisefikser.repository.EventTypeRepository;

import java.time.Instant;
import java.util.List;

/**
 * Service class for managing events.
 *
 * <p>
 * This class is responsible for handling event-related operations,
 * such as creating, updating, retrieving, and deleting events.
 * It may also include methods for retrieving events based on various criteria.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EventService {

  private static final Logger logger = LogManager.getLogger(EventService.class);
  private final EventRepository eventRepository;
  private final EventTypeRepository eventTypeRepository;
  private final EventMapper eventMapper;

  /**
   * Creates a new event based on the given request.
   *
   * @param request the {@link EventRequest} containing event details
   * @param eventType the {@link EventType} associated with the event
   * @return the created {@link EventResponse} DTO
   * @throws AppEntityNotFoundException if the specified event type does not exist
   */
  @Transactional
  public EventResponse createEvent(EventRequest request, EventType eventType) {
    logger.info("Adding new event with title: {}", request.getTitle());

    Event event = eventMapper.toEntity(request, eventType);

    if (request.getCircleData() != null) {
      logger.info("Adding circle data, circle data is {}", request.getCircleData());
      event.setCircleData(CircleDataMapper.toEntity(request.getCircleData()));
    }

    event = eventRepository.save(event);

    logger.info("Event added with ID: {}", event.getId());
    return eventMapper.toDto(event);
  }

  /**
   * Fetches all events from the database.
   *
   * @return a list of {@link EventResponse} DTOs representing all events
   */
  @Transactional
  public List<EventResponse> getAllEvents() {
    logger.info("Fetching all events");
    List<EventResponse> responses = eventRepository.findAll().stream()
        .map(eventMapper::toDto)
        .toList();
    logger.info("Fetched {} events", responses.size());
    return responses;
  }

  /**
   * Retrieves a single event by its ID.
   *
   * @param eventId the ID of the event to retrieve
   * @return the {@link EventResponse} DTO for the specified event
   * @throws AppEntityNotFoundException if no event with the given ID exists
   */
  @Transactional
  public EventResponse getEventById(Long eventId) {
    logger.info("Fetching event with ID: {}", eventId);
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.EVENT_NOT_FOUND));
    logger.info("Event found with ID: {}", eventId);
    return eventMapper.toDto(event);
  }

  /**
   * Updates an existing event with new details.
   *
   * @param eventId the ID of the event to update
   * @param request the {@link EventRequest} containing updated event details
   * @return the updated {@link EventResponse} DTO
   * @throws AppEntityNotFoundException if the event or event type does not exist
   * @throws InvalidGeoJsonException if the provided GeoJSON is invalid
   */
  @Transactional
  public EventResponse updateEvent(Long eventId, EventRequest request) {
    logger.info("Updating event with ID: {}", eventId);

    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.EVENT_NOT_FOUND));

    EventType eventType = eventTypeRepository.findById(request.getTypeId())
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.EVENT_TYPE_NOT_FOUND));

    event.setTitle(request.getTitle())
        .setDescription(request.getDescription())
        .setSeverity(request.getSeverity())
        .setStatus(request.getStatus())
        .setStartTime(Instant.parse(request.getStartTime()))
        .setType(eventType);

    if (request.getCircleData() != null) {
      logger.info("Updating circle data, circle data is {}", request.getCircleData());
      event.setCircleData(CircleDataMapper.toEntity(request.getCircleData()));
    }
    else {
      logger.info("Removing circle data");
      event.setCircleData(null);
    }

    try {
      GeoJsonReader reader = new GeoJsonReader();
      Geometry geometry = reader.read(request.getGeometryGeoJson());
      geometry.setSRID(4326);
      event.setGeometry(geometry);
    } catch (Exception e) {
      logger.error("Invalid GeoJSON format: {}", request.getGeometryGeoJson(), e);
      throw new InvalidGeoJsonException(CustomErrorMessage.GEOJSON_NOT_VALID);
    }

    event = eventRepository.save(event);
    logger.info("Event updated with ID: {}", event.getId());
    return eventMapper.toDto(event);
  }

  /**
   * Deletes an event by its ID.
   *
   * @param eventId the ID of the event to delete
   * @throws AppEntityNotFoundException if no event with the given ID exists
   */
  @Transactional
  public void deleteEvent(Long eventId) {
    logger.info("Deleting event with ID: {}", eventId);
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.EVENT_NOT_FOUND));
    eventRepository.delete(event);
    logger.info("Event deleted with ID: {}", eventId);
  }

  /**
   * Fetches events filtered by event type ID.
   *
   * @param eventTypeId the ID of the event type to filter by
   * @return a list of {@link EventResponse} DTOs for the specified event type
   * @throws AppEntityNotFoundException if no events exist for the given event type
   */
  @Transactional
  public List<EventResponse> getEventsByEventType(Long eventTypeId) {
    logger.info("Fetching events for typeId: {}", eventTypeId);
    List<Event> events = eventRepository.findEventByTypeId(eventTypeId);
    logger.info("Found {} events", events.size());
    if (events.isEmpty()) {
      logger.warn("No events found for typeId: {}", eventTypeId);
      throw new AppEntityNotFoundException(CustomErrorMessage.EVENT_NOT_FOUND);
    }
    return events.stream()
        .map(eventMapper::toDto)
        .toList();
  }
}
