package stud.ntnu.no.krisefikser.dtos.mappers;

import org.springframework.stereotype.Component;

import stud.ntnu.no.krisefikser.dtos.itemCategory.ItemCategoryRequest;
import stud.ntnu.no.krisefikser.dtos.itemCategory.ItemCategoryResponse;
import stud.ntnu.no.krisefikser.entities.ItemCategory;

/**
 * Mapper class for converting {@link ItemCategory} entities to CategoryResponse DTOs.
 * <p>
 * This class provides a static method to convert an {@link ItemCategory} entity to a {@link ItemCategoryResponse} DTO.
 * </p>
 */
@Component
public class ItemCategoryMapper {

  /**
   * Converts a {@link ItemCategory} entity to a {@link ItemCategoryResponse} DTO.
   *
   * @param itemCategory the {@link ItemCategory} entity to convert
   * @return the converted {@link ItemCategoryResponse} DTO
   */
  public static ItemCategoryResponse toDto(ItemCategory itemCategory) {
    if (itemCategory == null) {
      return null;
    }
    return new ItemCategoryResponse(itemCategory.getId(), itemCategory.getName());
  }

  /**
   * Converts a {@link ItemCategoryRequest} DTO to a {@link ItemCategory} entity.
   *
   * @param itemCategoryResponse the {@link ItemCategoryRequest} DTO to convert
   * @return the converted {@link ItemCategory} entity
   */
  public static ItemCategory toEntity(ItemCategoryRequest itemCategoryResponse) {
    if (itemCategoryResponse == null) {
      return null;
    }
    return new ItemCategory().setName(itemCategoryResponse.getName());
  }
}