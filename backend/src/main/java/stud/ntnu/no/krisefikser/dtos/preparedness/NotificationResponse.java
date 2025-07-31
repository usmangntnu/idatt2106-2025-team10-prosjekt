package stud.ntnu.no.krisefikser.dtos.preparedness;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import stud.ntnu.no.krisefikser.entities.NotificationType;

/**
 * DTO for returning notification details.
 * <p>
 * Contains information about a notification in the system, including its ID,
 * associated storage item details, and notification type.
 * </p>
 */
@Schema(
    description = "Notification data returned from the API.",
    name = "NotificationResponse"
)
@Data
public class NotificationResponse {
  /**
   * Unique identifier of the notification.
   */
  @Schema(
      description = "Unique identifier of the notification.",
      example = "1",
      required = true
  )
  private Long id;

  /**
   * ID of the storage item associated with this notification.
   * <p>
   * This field may be null if the notification is not linked to a specific storage item.
   * </p>
   */
  @Schema(
      description = "ID of the associated storage item (may be null).",
      example = "42",
      required = false
  )
  private Long storageItemId;

  /**
   * Name of the item associated with this notification.
   * <p>
   * Contains the name of the item definition linked to this notification's storage item.
   * </p>
   */
  @Schema(
      description = "Name of the item associated with this notification.",
      example = "Water Bottle",
      required = false
  )
  private String itemName;

  /**
   * Type of notification (e.g., expiration, low stock).
   * <p>
   * Indicates what this notification represents in the system.
   * </p>
   */
  @Schema(
      description = "Type of notification (EXPIRATION or LOW_STOCK).",
      example = "EXPIRATION",
      required = true
  )
  private NotificationType type;
}