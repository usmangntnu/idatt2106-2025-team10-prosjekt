package stud.ntnu.no.krisefikser.dtos.itemCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for creating item categories.
 * <p>
 * This class contains the title of the item category.
 * </p>
 */
@Schema(
  description = "Payload to create a new item category.",
  name        = "ItemCategoryRequest"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCategoryRequest {

  /**
   * Name of the item category.
   */
  @Schema(
    description = "Name of the item category.",
    example     = "Food",
    required    = true
  )
  @NotBlank(message = "Name cannot be blank")
  private String name;
}
