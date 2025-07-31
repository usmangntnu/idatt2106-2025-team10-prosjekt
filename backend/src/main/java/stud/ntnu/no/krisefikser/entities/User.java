package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
  /**
   * Unique identifier for the user.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  /**
   * First title of the user.
   */
  @Column(nullable = false)
  private String firstName;

  /**
   * Last title of the user.
   */
  @Column(nullable = false)
  private String lastName;

  /**
   * Encrypted password of the user.
   */
  @Column(nullable = false)
  private String password;

  /**
   * Email of the user.
   * Must be unique and not null.
   */
  @Column(unique = true)
  private String email;

  /**
   * Username of the user.
   * Must be unique and not null.
   */
  @Column(unique = true)
  private String username;
  
   /**
   * The household this user is associated with.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "household_id")
  private Household household;

  /**
   * Set of roles assigned to the user.
   * Determines the user's access permissions.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<UserRole> userRoles = new HashSet<>();


  /**
   * All the quizzes this user has taken.
   */
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<QuizAttempt> quizAttempts = new HashSet<>();

  /**
   * Indicates whether the user is enabled. Has to be manually set to true, this happens when the user
   * is verified through their email.
   */
  @Column(name = "enabled", nullable = false)
  private boolean enabled = false;

  /**
   * Returns the user's email which is used as username.
   * @return the email of the user.
   */
  public String getUsername() {
    return email;
  }

  /**
   * Checks if the user is the owner of the household.
   *
   * @return true if the user is the owner of the household, false otherwise.
   */
  public boolean isHouseholdAdmin() {
    return household != null &&
            household.getOwner() != null &&
            household.getOwner().getId().equals(this.id);
  }

  /**
   * Returns the authorities granted to the user.
   *
   * @return a collection of granted authorities.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    //TODO implement getauthorities correctly
    return List.of(new SimpleGrantedAuthority("USER"));
  }


  /**
   * Indicates whether the user's account has expired.
   * Always returns {@code true}, meaning accounts do not expire.
   *
   * @return {@code true} since the account never expires.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user's account is locked.
   * Always returns {@code true}, meaning accounts are never locked.
   *
   * @return {@code true} since the account is never locked.
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials (password) have expired.
   * Always returns {@code true}, meaning credentials never expire.
   *
   * @return {@code true} since credentials never expire.
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled.
   *
   * @return true if the user is enabled, false otherwise.
   */
  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
