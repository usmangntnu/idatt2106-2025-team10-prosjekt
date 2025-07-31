package stud.ntnu.no.krisefikser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.krisefikser.entities.User;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * <p>
 * Provides CRUD operations and custom queries for user entities.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Checks if a user exists with the given email.
   *
   * @param email the email to check for existence
   * @return {@code true} if a user with the given email exists, otherwise {@code false}
   */
  boolean existsByEmail(String email);

  /**
   * Finds a user by their email.
   *
   * @param email the email of the user to find
   * @return an {@link Optional} containing the user if found, otherwise empty
   */
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
}
