package stud.ntnu.no.krisefikser.dtos.storageItem;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for representing a storage item response with details such as quantity,
 * expiration date, and recommended usage per person.
 */
@Schema(
  description = "DTO representing the response for a storage item with its details.",
  name        = "StorageItemResponse"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageItemResponse {

  /**
   * The unique identifier of the storage item.
   */
  @Schema(
    description = "Identifier of the storage item.",
    example     = "123",
    required    = true
  )
  private Long id;

  /**
   * The category ID of the storage item
   */
  @Schema(
    description = "Identifier of the category to which the storage item belongs.",
    example     = "456",
    required    = true
  )
  private Long categoryId;

  /**
   * The currentStock of the storage item.
   */
  @Schema(
    description = "Current stock of the storage item.",
    example     = "10.0",
    required    = true
  )
  private double currentStock;

  /**
   * The expiration date of the storage item.
   */
  @Schema(
    description = "Expiration date of the storage item.",
    example     = "2023-12-31",
    required    = true
  )
  private Date expirationDate;

  /** 
   * The title of the storage item.
   */
  @Schema(
    description = "Title of the storage item.",
    example     = "Water",
    required    = true
  )
  private String name;

  /** 
   * The unit of measurement for the item (e.g., "kg", "liter"). 
   */
  @Schema(
    description = "Unit of measurement for the storage item.",
    example     = "kg",
    required    = true
  )
  private String unit;

  /** 
   * The recommended stock for a household. 
   */
  @Schema(
    description = "Recommended stock for a household.",
    example     = "5.0",
    required    = true
  )
  private double recommendedStockForHousehold;

  /**
   * The percentage of stock completion.
   */
  @Schema(
    description = "Percentage of stock completion.",
    example     = "75.0",
    required    = true
  )
  private double stockCompletionPercentage;
}
