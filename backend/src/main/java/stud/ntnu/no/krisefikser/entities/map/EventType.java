package stud.ntnu.no.krisefikser.entities.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Entity representing the type of event.
 * <p>
 * Every created event must have an event type, which is used to categorize the event.
 * Depending on the type of event, different actions may be taken.
 * </p>
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "EVENT_TYPE")
public class EventType {

  /**
   * Unique identifier for the event type.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  /**
   * Name of the event type.
   */
  @Column(name = "NAME", nullable = false, unique = true)
  private String name;

  /**
   * Description of the event type.
   */
  @Column(name = "DESCRIPTION", nullable = false)
  private String description;
}
