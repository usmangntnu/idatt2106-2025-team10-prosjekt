package stud.ntnu.no.krisefikser.controller;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import stud.ntnu.no.krisefikser.dtos.storageItem.StorageItemResponse;
import stud.ntnu.no.krisefikser.dtos.storageItem.StorageItemStockUpdateRequest;
import stud.ntnu.no.krisefikser.service.StorageItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Controller for handling storage item related operations.
 * <p>
 * Provides endpoints for managing storage items in the system.
 * </p>
 */
@RestController
@RequestMapping("/api/storageitems")
@RequiredArgsConstructor
@Tag(name = "Storage Items", description = "Endpoints for managing storage items")
public class StorageItemController {

  private static final Logger logger = LogManager.getLogger(StorageItemController.class);
  private final StorageItemService storageItemService;

  /**
   * Retrieves all storage items for a given Household Id and ItemCategory Id and
   * maps them to StorageItemResponse DTOs.
   * 
   * @param householdId the ID of the household
   * @param categoryId  the ID of the item category
   * @return a list of StorageItemResponse DTOs
   */
  @Operation(summary = "Get all storage items for a specific household and category", description = "Retrieves a list of all storage items for the given household and item category IDs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved storage items"),
      @ApiResponse(responseCode = "404", description = "Household or item category not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error while retrieving storage items")
  })
  @GetMapping("/{householdId}/{categoryId}")
  public List<StorageItemResponse> getStorageItemsByHouseholdAndCategory(@PathVariable Long householdId,
      @PathVariable Long categoryId) {
    logger.info("Fetching all storage items for household ID: {} and category ID: {}", householdId, categoryId);

    // Fetch storage items using the service
    List<StorageItemResponse> storageItems = storageItemService.getStorageItemsByHouseholdAndCategory(householdId,
        categoryId);

    // Log and return the results
    logger.info("Retrieved {} storage items for household ID: {} and category ID: {}", storageItems.size(), householdId,
        categoryId);
    return storageItems;
  }

  /**
   * Retrieves all storage items for a given Household and
   * maps them to StorageItemResponse DTOs.
   *
   * @param householdId the ID of the household
   * @return a list of StorageItemResponse DTOs
   */
  @Operation(summary = "Get all storage items for a specific household", description = "Retrieves a list of all storage items for the given household")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved storage items"),
      @ApiResponse(responseCode = "404", description = "Household not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error while retrieving storage items")
  })
  @GetMapping("/{householdId}")
  public List<StorageItemResponse> getStorageItemsByHousehold(@PathVariable Long householdId) {
    logger.info("Fetching all storage items for household ID: {}", householdId);

    // Fetch storage items using the service
    List<StorageItemResponse> storageItems = storageItemService.getStorageItemsByHousehold(householdId);

    // Log and return the results
    logger.info("Retrieved {} storage items for household ID: {}", storageItems.size(), householdId);

    return storageItems;
  }

  /**
   * Updates the stock quantity of a storage item.
   * 
   * @param request the request containing the storage item ID and new stock
   * @return the updated StorageItemResponse DTO
   */
  @Operation(summary = "Update stock of a storage item", description = "Updates the stock quantity of a specific storage item based on its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated the stock of the storage item"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "404", description = "Storage item not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error while updating stock")
  })
  @PutMapping("/update-stock")
  public StorageItemResponse updateStock(@Valid @RequestBody StorageItemStockUpdateRequest request) {
    logger.info("Attempting to update stock for storage item ID: {} to new quantity: {}",
        request.getId(), request.getNewStock());

    // Update stock using the service
    StorageItemResponse updatedItem = storageItemService.updateStock(request);

    // Log and return the updated item
    logger.info("Successfully updated stock for storage item ID: {} to new quantity: {}",
        updatedItem.getId(), updatedItem.getCurrentStock());
    return updatedItem;
  }

}
