package stud.ntnu.no.krisefikser.dtos.storageItem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.*;

/**
 * DTO for representing a request to update the stock of a storage item.
 */
@Schema(
  description = "Payload to update the stock of a storage item.",
  name        = "StorageItemStockUpdateRequest"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageItemStockUpdateRequest {

  /**
   * The unique identifier of the storage item.
   */
  @Schema(
    description = "Identifier of the storage item to be updated.",
    example     = "123",
    required    = true
  )
  @NotNull(message = "id cannot be null")
  @PositiveOrZero(message = "id cannot be negative")
  private Long id;

  /**
   * The new stock of the storage item.
   */
  @NotNull(message = "newStock cannot be null")
  @PositiveOrZero(message = "newStock cannot be negative")
  private Double newStock;
}
