package stud.ntnu.no.krisefikser.dtos.mappers;

import lombok.experimental.UtilityClass;
import stud.ntnu.no.krisefikser.dtos.map.event.EventTypeRequest;
import stud.ntnu.no.krisefikser.dtos.map.event.EventTypeResponse;
import stud.ntnu.no.krisefikser.entities.map.EventType;

/**
 * Utility class for mapping between EventType entities and their corresponding DTOs.
 * <p>
 *   This class provides methods to convert EventType entities to EventTypeResponse DTOs
 *   and to convert EventTypeRequest DTOs to EventType entities.
 * </p>
 */
@UtilityClass
public class EventTypeMapper {

  /**
   * Converts an EventType entity to an EventTypeResponse DTO.
   *
   * @param eventType the EventType entity to convert
   * @return the converted EventTypeResponse DTO
   */
  public static EventTypeResponse toDto(EventType eventType) {
    return new EventTypeResponse()
        .setId(eventType.getId())
        .setName(eventType.getName())
        .setDescription(eventType.getDescription());
  }

  public static EventType toEntity(EventTypeRequest eventTypeRequest) {
    return new EventType()
        .setName(eventTypeRequest.getName())
        .setDescription(eventTypeRequest.getDescription());
  }
}
