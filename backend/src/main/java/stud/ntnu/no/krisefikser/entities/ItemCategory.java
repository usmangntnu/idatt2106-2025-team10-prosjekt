package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a category of items.
 * <p>
 * Each category can group multiple items.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "item_category")
public class ItemCategory {

  /**
   * Unique identifier for the item category.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Name of the category.
   */
  @Column(nullable = false, unique = true)
  private String name;

  /**
   * List of itemDefinitions that belong to this category.
   */
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemDefinition> itemDefinitions = new ArrayList<>();
}
