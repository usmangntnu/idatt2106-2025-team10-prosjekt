package stud.ntnu.no.krisefikser.entities.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Entity representing a position type on the map, such as shelter, food-aid center, etc.
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "POSITION")
public class Position {

  /**
   * Unique identifier for the position
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  /**
   * Title of the position. For example, "Food-Shelter in Oslo"
   */
  @Column(name = "TITLE", nullable = false)
  private String title;

  /**
   * Description of the position. For example, "A food-shelter with a capacity of 100 people"
   */
  @Column(name = "DESCRIPTION", nullable = false)
  private String description;

  /**
   * Date and time when the position was created. Created automatically by the database.
   */
  @CreationTimestamp
  @Column(name = "CREATED_AT", nullable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /**
   * Latitude of the position.
   */
  @Column(name = "LATITUDE", nullable = false)
  private double latitude;

  /**
   * Longitude of the position.
   */
  @Column(name = "LONGITUDE", nullable = false)
  private double longitude;

  /**
   * Capacity of the position. For example, "100" for a shelter that can accommodate 100 people.
   * This is an optional field, only to be chosen for shelters or other positions with capacity.
   */
  @Column(name = "CAPACITY")
  private Integer capacity;

  /**
   * Type of the position. For example, "SHELTER", "FOOD_AID", etc. This is an entity, position type.
   */
  @ManyToOne
  @JoinColumn(name = "POSITION_TYPE_ID", referencedColumnName = "ID", nullable = false)
  private PositionType positionType;
}

