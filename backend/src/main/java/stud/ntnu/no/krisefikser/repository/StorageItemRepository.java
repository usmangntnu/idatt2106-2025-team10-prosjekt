package stud.ntnu.no.krisefikser.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.no.krisefikser.entities.StorageItem;

/**
 * Repository interface for managing StorageItem entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations.
 * </p>
 */
@Repository
public interface StorageItemRepository extends JpaRepository<StorageItem, Long> {

  /**
   * Finds all storage items for a given household and item category.
   *
   * @param householdId the ID of the household
   * @param categoryId the ID of the item category
   * @return a list of StorageItem entities matching the criteria
   */
  List<StorageItem> findByHouseholdIdAndItemDefinition_CategoryId(Long householdId, Long categoryId);

  /**
   * Checks if a storage item exists by household ID and item definition ID.
   *
   * @param householdId the ID of the household
   * @param itemDefinitionId the ID of the item definition
   * @return true if the storage item exists, false otherwise
   */
  boolean existsByHouseholdIdAndItemDefinitionId(Long householdId, Long itemDefinitionId);

  /**
   * Finds all storage items belonging to a specific household.
   *
   * @param householdId the ID of the household
   * @return a list of StorageItem entities belonging to the household
   */
  List<StorageItem> findByHouseholdId(Long householdId);
}
