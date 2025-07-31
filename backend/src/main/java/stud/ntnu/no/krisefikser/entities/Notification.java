package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Entity representing a notification in the system.
 * <p>
 * Notifications can be of different types and are associated with a specific
 * item.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "notifications")
public class Notification {

  /**
   * Unique identifier for the notification.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The household this notification belongs to.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  /**
   * The storageItem associated with this notification.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "storage_item_id")
  private StorageItem storageItem;

  /**
   * The type of notification (e.g., expiration, low stock).
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;
}
