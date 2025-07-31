package stud.ntnu.no.krisefikser.entities.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Entity representing the type of position, such as shelter, food-aid center, etc.
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "POSITION_TYPE")
public class PositionType {

  /**
   * Unique identifier for the position type.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  /**
   * Name of the position type. For example, "Shelter", "Food-Aid", "Medical".
   */
  @Column(name = "NAME", nullable = false, unique = true)
  private String name;

  /**
   * Optional description of the position type. For example, "Locations that provide temporary shelter."
   */
  @Column(name = "DESCRIPTION")
  private String description;

}
