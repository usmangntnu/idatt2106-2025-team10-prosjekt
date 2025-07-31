package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 * Entity representing a quantity of a specific itemDefinition.
 * <p>
 * This is a join between an itemDefinition and a household, with metadata such
 * as
 * current stock and last restocked date.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "storage_item")
public class StorageItem {

  /**
   * Unique identifier for the storage item.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The itemDefinition this storage record refers to.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id", nullable = false)
  private ItemDefinition itemDefinition;

  /**
   * The household this item belongs to.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  /**
   * Quantity of the item stored.
   */
  @Column(nullable = false)
  private double currentStock;

  /**
   * Timestamp of when the Item was last restocked.
   * Defaults to the current date and time when the record is created.
   */
  @CreationTimestamp
  @Column(name = "last_updated", nullable = false)
  private Date lastRestockedAt;
}
