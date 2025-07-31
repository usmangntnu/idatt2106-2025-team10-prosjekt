package stud.ntnu.no.krisefikser.entities;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representing the different types of notifications in the system.
 * <p>
 * Each type represents a specific notification scenario that can be triggered.
 * </p>
 */
@Schema(description = "Types of notifications available in the system")
public enum NotificationType {
    /**
     * Notification for items that are expiring or have expired.
     */
    @Schema(description = "Notification for items that are expiring or have expired")
    EXPIRATION,

    /**
     * Notification for items with low inventory levels.
     */
    @Schema(description = "Notification for items with low inventory levels")
    LOW_STOCK
}