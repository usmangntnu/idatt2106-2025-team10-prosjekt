package stud.ntnu.no.krisefikser.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.dtos.map.event.EventTypeRequest;
import stud.ntnu.no.krisefikser.dtos.map.event.EventTypeResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.EventTypeMapper;
import stud.ntnu.no.krisefikser.entities.map.EventType;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.krisefikser.repository.EventTypeRepository;

/**
 * Service class for managing event types.
 *
 * <p>
 * This class is responsible for handling event-type-related operations,
 * such as creating, updating, and deleting event types.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EventTypeService {

  private static final Logger logger = LogManager.getLogger(EventTypeService.class);
  private final EventTypeRepository eventTypeRepository;

  /**
   * This method fetches all event types from the repository, by utilizing the
   * {@link EventTypeMapper} to convert them into DTOs. The {@link EventTypeRepository}
   * is used to access the database and retrieve the event types.
   *
   * @return a list of {@link EventTypeResponse} objects representing all available event types
   */
  @Transactional
  public List<EventTypeResponse> getAllEventTypes() {
    logger.info("Fetching all event types");
    List<EventTypeResponse> eventTypes = eventTypeRepository.findAll().stream()
        .map(EventTypeMapper::toDto)
        .toList();
    logger.info("Fetched {} event types", eventTypes.size());
    return eventTypes;
  }

  /**
   * This method fetches an event type from the repository using its ID.
   * The {@link EventTypeRepository} is used to access the database.
   *
   * @param eventTypeId the ID of the event type to retrieve
   * @return the {@link EventType} entity
   * @throws AppEntityNotFoundException if the event type is not found
   */
  @Transactional
  public EventTypeResponse getEventTypeById(long eventTypeId) {
    logger.info("Fetching event type with ID {}", eventTypeId);

    EventType eventType = eventTypeRepository.findById(eventTypeId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.EVENT_TYPE_NOT_FOUND));
    logger.info("Fetched event type: {}", eventType.getName());

    return EventTypeMapper.toDto(eventType);
  }

  /**
   * This method saves a new event type to the repository.
   * The {@link EventTypeRepository} is used to access the database.
   *
   * @param request the {@link EventType} entity to create
   * @return the created {@link EventTypeResponse} DTO
   */
  @Transactional
  public EventTypeResponse createEventType(EventTypeRequest request) {
    logger.info("Creating new event type: {}", request.getName());

    if (eventTypeRepository.existsByName(request.getName())) {
      logger.warn("Event type with title '{}' already exists", request.getName());
      throw new EntityAlreadyExistsException(CustomErrorMessage.EVENT_TYPE_ALREADY_EXISTS);
    }

    EventType eventType = new EventType()
        .setName(request.getName())
        .setDescription(request.getDescription());
    eventType = eventTypeRepository.save(eventType);

    logger.info("Created event type with ID: {}", eventType.getId());
    return EventTypeMapper.toDto(eventType);
  }

  /**
   * Method to delete an event type from the database.
   *
   * @param eventTypeId the ID of the event type to delete
   */
  @Transactional
  public void deleteEventType(long eventTypeId) {
    logger.info("Deleting event type with ID {}", eventTypeId);

    EventType eventType = eventTypeRepository.findById(eventTypeId)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.EVENT_TYPE_NOT_FOUND));

    eventTypeRepository.delete(eventType);
    logger.info("Deleted event type with ID: {}", eventTypeId);
  }
}
