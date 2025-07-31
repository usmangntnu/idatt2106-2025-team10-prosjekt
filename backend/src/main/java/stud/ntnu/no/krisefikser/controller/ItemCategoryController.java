package stud.ntnu.no.krisefikser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import stud.ntnu.no.krisefikser.dtos.itemCategory.ItemCategoryRequest;
import stud.ntnu.no.krisefikser.dtos.itemCategory.ItemCategoryResponse;
import stud.ntnu.no.krisefikser.service.ItemCategoryService;

import java.util.List;

/**
 * Controller for handling itemCategory related operations.
 * <p>
 * Provides endpoints for managing item categories in the system.
 * </p>
 */
@RestController
@RequestMapping("/api/itemcategories")
@RequiredArgsConstructor
@Tag(name = "Item Categories", description = "Endpoints for managing item categories")
public class ItemCategoryController {

  private static final Logger logger = LogManager.getLogger(ItemCategoryController.class);
  private final ItemCategoryService itemCategoryService;

  /**
   * Retrieves all item categories.
   *
   * @return a list of {@link ItemCategoryResponse} DTOs representing all item categories
   */
  @Operation(summary = "Get all item categories", description = "Retrieves a list of all item categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved item categories"),
      @ApiResponse(responseCode = "500", description = "Internal server error while retrieving item categories")
  })
  @GetMapping
  public List<ItemCategoryResponse> getAllItemCategories() {
    logger.info("Fetching all item categories");
    List<ItemCategoryResponse> categories = itemCategoryService.getAllItemCategories();
    logger.info("Retrieved {} item categories", categories.size());
    return categories;
  }

  /**
   * Creates a new item category.
   *
   * @param itemCategoryRequest the request containing the title of the new item category
   * @return the created {@link ItemCategoryResponse} DTO
   */
  @Operation(summary = "Create new item category", description = "Adds a new item category to the system")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Item category created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "500", description = "Internal server error while creating item category"),
      @ApiResponse(responseCode = "409", description = "Item category already exists")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ItemCategoryResponse createCategory(@Valid @RequestBody ItemCategoryRequest itemCategoryRequest) {
    logger.info("Creating new item category: {}", itemCategoryRequest.getName());
    ItemCategoryResponse createdCategory = itemCategoryService.createCategory(itemCategoryRequest);
    logger.info("Created new item category with ID: {}", createdCategory.getId());
    return createdCategory;
  }
}
