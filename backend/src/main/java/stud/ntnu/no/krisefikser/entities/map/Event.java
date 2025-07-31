  package stud.ntnu.no.krisefikser.entities.map;

  import jakarta.persistence.*;
  import lombok.Getter;
  import lombok.Setter;
  import lombok.experimental.Accessors;
  import org.hibernate.annotations.CreationTimestamp;
  import org.hibernate.annotations.UpdateTimestamp;
  import org.locationtech.jts.geom.Geometry;

  import java.time.Instant;

  /**
   * Entity representing a real-world event, such as a natural disaster or security incident.
   */
  @Getter
  @Setter
  @Accessors(chain = true)
  @Entity
  @Table(name = "EVENT")
  public class Event {

    /**
     * Unique identifier for the event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    /**
     * Title of the event. For example, "Earthquake in Oslo".
     */
    @Column(name = "TITLE", nullable = false)
    private String title;

    /**
     * Description of the event. For example, "An earthquake with a magnitude of 5.0 occurred in Oslo".
     */
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    /**
     * Severity of the event. For example, "Safe", "Warning", "Danger".
     */
    @Column(name = "SEVERITY", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventSeverity severity;

    /**
     * Date and time when the event was created.
     */
    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    /**
     * Date and time when the event was last updated.
     */
    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    /**
     * Start time (Date and Time) of the event, as determined by the admin user
     */
    @Column(name = "START_TIME", nullable = false)
    private Instant startTime;

    /**
     * Status of the event. For example, "Active", "Inactive", etc. Uses {@link EventStatus} enum.
     */
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    /**
     * GeoJSON representation of the event's geometry.
     */
    @Lob
    @Column(name = "GEOMETRY", columnDefinition = "geometry")
    private Geometry geometry;

    /**
     * The type of the event. For example, "Natural Disaster", "Aerial Assault", etc.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "EVENT_TYPE_ID", referencedColumnName = "ID", nullable = false)
    private EventType type;

  /**
   * An embedded type representing the geometry of a circle, if an event is drawn
   * circularly on the map.
   *
   * @see CircleData
   */
  @Embedded
  private CircleData circleData;
  }
