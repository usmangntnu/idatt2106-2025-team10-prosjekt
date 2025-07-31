package stud.ntnu.no.krisefikser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.krisefikser.entities.map.PositionType;

import java.util.Optional;

/**
 * Repository interface for managing {@link PositionType} entities.
 * <p>
 *   Provides CRUD operations and custom queries for position types.
 * </p>
 */
public interface PositionTypeRepository extends JpaRepository<PositionType, Long> {

  /**
   * Find all position types by their title.
   *
   * @param name the title of the position type
   * @return a list of position types with the specified title
   */
  Optional<PositionType> findByName(String name);

  /**
   * Checks if a position type with the specified title exists.
   *
   * @param name the title of the position type to check
   * @return {@code true} if a position type with the specified title exists, otherwise {@code false}
   */
  boolean existsByName(String name);
}
