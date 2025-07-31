package stud.ntnu.no.krisefikser.dtos.preparedness;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import stud.ntnu.no.krisefikser.entities.NotificationType;

/**
 * DTO for creating a notification.
 * <p>
 * Contains the necessary data for creating a new notification in the system,
 * specifying the household, optional storage item, and notification type.
 * </p>
 */
@Schema(
    description = "Request payload for creating a new notification.",
    name = "NotificationRequest"
)
@Data
public class NotificationRequest {
  /**
   * ID of the household this notification belongs to.
   */
  @Schema(
      description = "ID of the household this notification belongs to.",
      example = "1",
      required = true
  )
  @NotNull(message = "Household ID is required")
  private Long householdId;

  /**
   * ID of the storage item associated with this notification.
   * <p>
   * This field is optional. If provided, the notification will be linked to a specific storage item.
   * </p>
   */
  @Schema(
      description = "ID of the storage item associated with this notification (optional).",
      example = "42",
      required = false
  )
  private Long storageItemId;

  /**
   * Type of notification (e.g., expiration, low stock).
   * <p>
   * Defines what this notification represents in the system.
   * </p>
   */
  @Schema(
      description = "Type of notification (EXPIRATION or LOW_STOCK).",
      example = "EXPIRATION",
      required = true
  )
  @NotNull(message = "Notification type is required")
  private NotificationType type;
}