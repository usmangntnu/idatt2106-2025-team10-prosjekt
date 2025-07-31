package stud.ntnu.no.krisefikser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.krisefikser.entities.map.Position;

import java.util.List;

/**
 * Repository interface for managing {@link Position} entities.
 * <p>
 *   Provides CRUD operations and custom queries for position types.
 * </p>
 */
public interface PositionRepository extends JpaRepository<Position, Long> {

  /**
   * Find all positions by the type ID.
   *
   * @param typeId the ID of the position type
   */
  List<Position> findAllByPositionTypeId(Long typeId);

  /**
   * Find all positions by the type title.
   *
   * @param typeName the title of the position type
   */
  List<Position> findAllByPositionTypeName(String typeName);

  /**
   * Checks if a position with the specified title exists.
   *
   * @param title the title of the position to check
   * @return {@code true} if a position with the specified title exists, otherwise {@code false}
   */
  boolean existsByTitle(String title);
}