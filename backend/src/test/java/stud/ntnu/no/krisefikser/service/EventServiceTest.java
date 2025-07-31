package stud.ntnu.no.krisefikser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.krisefikser.dtos.map.event.CircleDataDto;
import stud.ntnu.no.krisefikser.dtos.map.event.EventRequest;
import stud.ntnu.no.krisefikser.dtos.map.event.EventResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.EventMapper;
import stud.ntnu.no.krisefikser.entities.map.Event;
import stud.ntnu.no.krisefikser.entities.map.EventSeverity;
import stud.ntnu.no.krisefikser.entities.map.EventType;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.InvalidGeoJsonException;
import stud.ntnu.no.krisefikser.repository.EventRepository;
import stud.ntnu.no.krisefikser.repository.EventTypeRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock
  private EventRepository eventRepository;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @Mock
  private EventMapper eventMapper;

  @InjectMocks
  private EventService eventService;

  private EventRequest request;
  private EventType type;
  private Event entity;
  private EventResponse response;

  @BeforeEach
  void setUp() {
    request = new EventRequest();
    request.setTitle("Test Event");
    request.setDescription("Sample description");
    request.setSeverity(EventSeverity.MEDIUM);
    request.setTypeId(99L);
    request.setGeometryGeoJson("{\"type\":\"Point\",\"coordinates\":[0,0]}");
    request.setStartTime("2023-10-01T12:00:00Z");

    type = new EventType();
    type.setId(99L);

    entity = new Event();
    entity.setId(1L);
    entity.setTitle(request.getTitle());

    response = new EventResponse();
    response.setId(entity.getId());
    response.setTitle(entity.getTitle());
  }

  @Test
  void getAllEvents_returnsMappedList() {
    Event e1 = new Event(); e1.setId(1L);
    Event e2 = new Event(); e2.setId(2L);
    EventResponse r1 = new EventResponse(); r1.setId(1L);
    EventResponse r2 = new EventResponse(); r2.setId(2L);
    when(eventRepository.findAll()).thenReturn(List.of(e1, e2));
    when(eventMapper.toDto(e1)).thenReturn(r1);
    when(eventMapper.toDto(e2)).thenReturn(r2);

    List<EventResponse> all = eventService.getAllEvents();

    assertEquals(2, all.size());
    assertIterableEquals(List.of(r1, r2), all);
  }

  @Test
  void getEventById_exists_returnsDto() {
    when(eventRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(eventMapper.toDto(entity)).thenReturn(response);

    EventResponse actual = eventService.getEventById(1L);

    assertEquals(response, actual);
  }

  @Test
  void getEventById_missing_throwsNotFound() {
    when(eventRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AppEntityNotFoundException.class,
        () -> eventService.getEventById(1L));
  }

  @Test
  void deleteEvent_success() {
    when(eventRepository.findById(1L)).thenReturn(Optional.of(entity));

    eventService.deleteEvent(1L);

    verify(eventRepository).delete(entity);
  }

  @Test
  void deleteEvent_missing_throwsNotFound() {
    when(eventRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AppEntityNotFoundException.class,
        () -> eventService.deleteEvent(1L));
  }

  @Test
  void getEventsByEventType_success() {
    when(eventRepository.findEventByTypeId(99L)).thenReturn(List.of(entity));
    when(eventMapper.toDto(entity)).thenReturn(response);

    List<EventResponse> list = eventService.getEventsByEventType(99L);

    assertEquals(1, list.size());
  }

  @Test
  void getEventsByEventType_empty_throwsNotFound() {
    when(eventRepository.findEventByTypeId(99L)).thenReturn(Collections.emptyList());

    assertThrows(AppEntityNotFoundException.class,
        () -> eventService.getEventsByEventType(99L));
  }

  @Test
  void createEvent_success() {
    request.setCircleData(new CircleDataDto(1.0, 2.0, 3.0)); // Replace with your actual DTO class
    when(eventMapper.toEntity(request, type)).thenReturn(entity);
    when(eventRepository.save(entity)).thenReturn(entity);
    when(eventMapper.toDto(entity)).thenReturn(response);

    EventResponse result = eventService.createEvent(request, type);

    assertEquals(response, result);
    verify(eventRepository).save(entity);
  }

  @Test
  void updateEvent_success() {
    when(eventRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(eventTypeRepository.findById(request.getTypeId())).thenReturn(Optional.of(type));
    when(eventRepository.save(any(Event.class))).thenReturn(entity);
    when(eventMapper.toDto(any(Event.class))).thenReturn(response);

    EventResponse result = eventService.updateEvent(1L, request);

    assertEquals(response, result);
    verify(eventRepository).save(any(Event.class));
  }

  @Test
  void updateEvent_eventNotFound_throwsException() {
    when(eventRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(AppEntityNotFoundException.class, () ->
        eventService.updateEvent(1L, request));
  }

  @Test
  void updateEvent_eventTypeNotFound_throwsException() {
    when(eventRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(eventTypeRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(AppEntityNotFoundException.class, () ->
        eventService.updateEvent(1L, request));
  }

  @Test
  void updateEvent_invalidGeoJson_throwsException() {
    request.setGeometryGeoJson("INVALID_JSON");

    when(eventRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(eventTypeRepository.findById(99L)).thenReturn(Optional.of(type));

    assertThrows(InvalidGeoJsonException.class, () ->
        eventService.updateEvent(1L, request));
  }

}
