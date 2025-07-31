package stud.ntnu.no.krisefikser.dtos.itemCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for returning category details.
 */
@Schema(
  description = "DTO representing the response for an item category with its details.",
  name        = "ItemCategoryResponse"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCategoryResponse {

  /**
   * Unique identifier for the category.
   */
  @Schema(
    description = "Identifier of the item category.",
    example     = "1",
    required    = true
  )
  private Long id;

  /**
   * Name of the category.
   */
  @Schema(
    description = "Name of the item category.",
    example     = "Food",
    required    = true
  )
  private String name;
}
