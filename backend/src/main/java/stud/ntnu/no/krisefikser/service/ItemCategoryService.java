package stud.ntnu.no.krisefikser.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import stud.ntnu.no.krisefikser.dtos.itemCategory.ItemCategoryRequest;
import stud.ntnu.no.krisefikser.dtos.itemCategory.ItemCategoryResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.ItemCategoryMapper;
import stud.ntnu.no.krisefikser.entities.ItemCategory;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.krisefikser.repository.ItemCategoryRepository;

import java.util.List;

/**
 * Service class for managing ItemCategory entities.
 * <p>
 * This class provides a method to retrieve all item categories and convert them
 * to DTOs.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ItemCategoryService {

  private static final Logger logger = LogManager.getLogger(ItemCategoryService.class);
  private final ItemCategoryRepository categoryRepository;

  /**
   * Retrieves all item categories and converts them to DTOs.
   *
   * @return a list of {@link ItemCategoryResponse} DTOs representing all item categories
   */
  public List<ItemCategoryResponse> getAllItemCategories() {
    logger.info("Fetching all item categories from the database");
    List<ItemCategoryResponse> categories = categoryRepository.findAll().stream()
        .map(ItemCategoryMapper::toDto)
        .toList();
    logger.info("Retrieved {} item categories", categories.size());
    return categories;
  }

  /**
   * Creates a new item category based on the provided request.
   *
   * @param itemCategoryRequest the request containing the details of the new item category
   * @return the created {@link ItemCategoryResponse} DTO
   * @throws EntityAlreadyExistsException if an item category with the same title already exists
   */
  public ItemCategoryResponse createCategory(ItemCategoryRequest itemCategoryRequest) {
    String name = itemCategoryRequest.getName();
    logger.info("Creating new item category: {}", itemCategoryRequest.getName());
    
    // Check if the category already exists
    if(categoryRepository.existsByNameIgnoreCase(name)) {
      logger.error("Item category with title '{}' already exists", name);
      throw new EntityAlreadyExistsException(CustomErrorMessage.ITEM_CATEGORY_ALREADY_EXISTS);
    }

    ItemCategory itemCategory = ItemCategoryMapper.toEntity(itemCategoryRequest);
    ItemCategory savedItemCategory = categoryRepository.save(itemCategory);
    logger.info("Created new item category with ID: {}", savedItemCategory.getId());
    return ItemCategoryMapper.toDto(savedItemCategory);
  }

  /**
   * Checks if an item category with the specified ID exists in the database.
   *
   * @param id the ID of the item category to check
   * @return true if the item category exists, false otherwise
   */
  public boolean existsById(Long id) {
    logger.info("Checking if item category with ID {} exists", id);
    boolean exists = categoryRepository.existsById(id);
    logger.info("Item category with ID {} exists: {}", id, exists);
    return exists;
  }
}
