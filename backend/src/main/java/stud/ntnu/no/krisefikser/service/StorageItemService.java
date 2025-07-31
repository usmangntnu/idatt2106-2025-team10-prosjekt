package stud.ntnu.no.krisefikser.service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stud.ntnu.no.krisefikser.dtos.mappers.StorageItemMapper;
import stud.ntnu.no.krisefikser.dtos.storageItem.StorageItemResponse;
import stud.ntnu.no.krisefikser.dtos.storageItem.StorageItemStockUpdateRequest;
import stud.ntnu.no.krisefikser.entities.StorageItem;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.StorageItemRepository;

/**
 * Service class for managing storage items.
 * <p>
 * This class provides methods to retrieve storage items and update their stock levels.
 * It also integrates with the notification system to generate alerts for low stock
 * and expiring items.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class StorageItemService {

  private static final Logger logger = LogManager.getLogger(StorageItemService.class);

  private final StorageItemRepository storageItemRepository;
  private final HouseholdService householdService;
  private final ItemCategoryService categoryService;
  private final NotificationService notificationService;
  private static final double EPSILON = 0.001; // Tolerance for floating point comparison

  /**
   * Retrieves all storage items for a given Household Id and Item Category Id
   * and maps them to StorageItemResponse DTOs.
   *
   * @param householdId the ID of the household
   * @param categoryId  the ID of the item category
   * @return a list of StorageItemResponse DTOs
   */
  @Transactional
  public List<StorageItemResponse> getStorageItemsByHouseholdAndCategory(Long householdId, Long categoryId) {
    logger.info("Fetching all storage items for household ID: {} and category ID: {}", householdId, categoryId);

    // Check if household exists
    if (!householdService.existsById(householdId)) {
      logger.error("Household with ID {} not found", householdId);
      throw new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND);
    }

    // Check if category exists
    if (!categoryService.existsById(categoryId)) {
      logger.error("Item category with ID {} not found", categoryId);
      throw new AppEntityNotFoundException(CustomErrorMessage.ITEM_CATEGORY_NOT_FOUND);
    }

    // Fetch storage items based on household and category
    List<StorageItem> storageItems = storageItemRepository.findByHouseholdIdAndItemDefinition_CategoryId(householdId,
        categoryId);

    // Map the list of StorageItem entities to StorageItemResponse DTOs
    List<StorageItemResponse> storageItemResponses = storageItems.stream()
        .map(StorageItemMapper::toDto)
        .toList();

    logger.info("Retrieved {} storage items for household ID: {} and category ID: {}", storageItemResponses.size(),
        householdId, categoryId);

    return storageItemResponses;
  }

  /**
   * Retrieves all storage items for a given Household Id
   * and maps them to StorageItemResponse DTOs.
   *
   * @param householdId the ID of the household
   * @return a list of StorageItemResponse DTOs
   */
  @Transactional
  public List<StorageItemResponse> getStorageItemsByHousehold(Long householdId) {
    // Check if household exists
    if (!householdService.existsById(householdId)) {
      logger.error("Household with ID {} not found", householdId);
      throw new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND);
    }

    // Fetch storage items based on household
    List<StorageItem> storageItems = storageItemRepository.findByHouseholdId(householdId);

    // Map the list of StorageItem entities to StorageItemResponse DTOs
    List<StorageItemResponse> storageItemResponses = storageItems.stream()
        .map(StorageItemMapper::toDto)
        .toList();

    logger.info("Retrieved {} storage items for household ID: {}", storageItemResponses.size(), householdId);

    return storageItemResponses;
  }

  /**
   * Updates the stock quantity of a storage item and checks for low stock and expiration conditions.
   *
   * @param request the request containing the storage item ID and new stock
   * @return the updated StorageItem entity
   */

  @Transactional
  public StorageItemResponse updateStock(StorageItemStockUpdateRequest request) {
    logger.info("Updating stock for storage item ID: {} to new stock: {}", request.getId(), request.getNewStock());

    StorageItem storageItem = storageItemRepository.findById(request.getId())
        .orElseThrow(() -> {
          logger.error("Storage item with ID {} not found", request.getId());
          return new AppEntityNotFoundException(CustomErrorMessage.STORAGE_ITEM_NOT_FOUND);
        });

    double oldStock = storageItem.getCurrentStock();
    double newStock = request.getNewStock();

    storageItem.setCurrentStock(newStock);

    if (newStock > oldStock) {
      storageItem.setLastRestockedAt(Date.from(Instant.now()));
      logger.info("Set last restocked date to current date for storage item ID: {}", request.getId());
    } else if (Math.abs(oldStock - newStock) < EPSILON) {
      storageItem.setLastRestockedAt(Date.from(Instant.now()));
      logger.info("Stock unchanged for storage item ID: {}", request.getId());
    } else {
      logger.info("Stock decreased, leaving timestamp unchanged: {}", request.getId());
    }

    StorageItem updated = storageItemRepository.saveAndFlush(storageItem);
    logger.info("Successfully updated stock for storage item ID: {} to new quantity: {}", request.getId(), newStock);

    notificationService.checkAndGenerateNotifications(updated);

    StorageItemResponse response = StorageItemMapper.toDto(updated);
    logger.info("Mapped updated storage item to DTO: {}", response);

    return response;
  }
}
