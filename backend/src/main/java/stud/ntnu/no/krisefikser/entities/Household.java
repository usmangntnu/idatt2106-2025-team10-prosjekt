package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * Entity representing a household in the system.
 * <p>
 * Each household has a unique identifier, a title, and a list of users
 * associated with it.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "household")
public class Household {
  /**
   * Unique identifier for the household.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Name of the household.
   */
  @Column(nullable = false)
  private String name;

  /**
   * The address of the household.
   */
  private String address;

  /**
   * The postal code of the household.
   */
  private String postalCode;

  /**
   * The city of the household.
   */
  private String city;

  /**
   * The latitude of the household.
   */
  private Double latitude;

  /**
   * The longitude of the household.
   */
  private Double longitude;

  /**
   * List of users associated with the household.
   */
  @OneToMany(mappedBy = "household")
  private List<User> users = new ArrayList<>();

  /**
   * List of household tokens associated with the household.
   */
  @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<HouseholdToken> tokens = new HashSet<>();

  /**
   * List of storageItems associated with the household.
   */
  @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<StorageItem> storageItems = new ArrayList<>();

  @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notification> notifications = new ArrayList<>();

  @OneToOne()
  @JoinColumn(name = "owner_id")
  private User owner;
}
