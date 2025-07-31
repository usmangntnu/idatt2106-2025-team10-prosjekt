package stud.ntnu.no.krisefikser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.no.krisefikser.entities.ItemDefinition;

/**
 * Repository interface for managing Item entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations.
 * </p>
 */
@Repository
public interface ItemDefinitionRepository extends JpaRepository<ItemDefinition, Long> {

}
