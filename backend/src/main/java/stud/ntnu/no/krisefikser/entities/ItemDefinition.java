package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Entity representing a predefined item in the system.
 * <p>
 * ItemDefinitions have a title, belong to a category, define units, recommended
 * quantities and shelf life.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "item_definitions")
public class ItemDefinition {

  /**
   * Unique identifier for the item.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Name of the item.
   */
  @Column(nullable = false)
  private String name;

  /**
   * The category this item belongs to.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private ItemCategory category;

  /**
   * The unit this item is measured in (e.g., liters, kilograms).
   */
  private String unit;

  /**
   * Recommended quantity per person.
   */
  @Column(name = "recommended_amount_per_person", nullable = false)
  private Double recommendedAmountPerPerson;

  /**
   * Shelf life of the item in days.
   */
  @Column(name = "shelf_life_days", nullable = false)
  private int shelfLifeDays;
}
