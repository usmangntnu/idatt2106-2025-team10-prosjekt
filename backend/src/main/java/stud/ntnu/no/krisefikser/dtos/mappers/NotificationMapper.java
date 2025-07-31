package stud.ntnu.no.krisefikser.dtos.mappers;

import stud.ntnu.no.krisefikser.dtos.preparedness.NotificationResponse;
import stud.ntnu.no.krisefikser.entities.Notification;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting {@link Notification} entities to {@link NotificationResponse} DTOs.
 * <p>
 * This class provides methods to transform notification entities into data transfer objects
 * suitable for client communication, hiding implementation details and database relationships.
 * </p>
 */
public class NotificationMapper {

  /**
   * Converts a {@link Notification} entity to a {@link NotificationResponse} DTO.
   * <p>
   * Maps the notification data including the associated storage item details if present.
   * </p>
   *
   * @param notification the {@link Notification} entity to convert
   * @return the converted {@link NotificationResponse} DTO
   */
  public static NotificationResponse toDto(Notification notification) {
    NotificationResponse dto = new NotificationResponse();
    dto.setId(notification.getId());
    if (notification.getStorageItem() != null) {
      dto.setStorageItemId(notification.getStorageItem().getId());

      // Map name via itemDefinition if available
      if (notification.getStorageItem().getItemDefinition() != null) {
        dto.setItemName(notification.getStorageItem().getItemDefinition().getName());
      }
    }
    dto.setType(notification.getType());
    return dto;
  }

  /**
   * Converts a list of {@link Notification} entities to a list of {@link NotificationResponse} DTOs.
   * <p>
   * Applies the toDto conversion to each entity in the provided list.
   * </p>
   *
   * @param notifications the list of {@link Notification} entities to convert
   * @return a list of converted {@link NotificationResponse} DTOs
   */
  public static List<NotificationResponse> toDtoList(List<Notification> notifications) {
    return notifications.stream()
        .map(NotificationMapper::toDto)
        .collect(Collectors.toList());
  }
}
