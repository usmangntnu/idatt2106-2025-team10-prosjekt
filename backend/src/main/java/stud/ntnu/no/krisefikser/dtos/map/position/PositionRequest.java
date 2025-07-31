package stud.ntnu.no.krisefikser.dtos.map.position;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO representing the request for a position.
 * <p>
 *   This class is used to encapsulate the data required to create or update a position.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO representing the request for a position type, including metadata like " +
    "title and description.")
public class PositionRequest {

  @Schema(description = "Title of the position type", example = "Shelter")
  private String title;

  @Schema(description = "Description of the position type", example = "A place where people can take shelter.")
  private String description;

  @Schema(description = "Latitude of the position type", example = "63.4305")
  private double latitude;

  @Schema(description = "Longitude of the position type", example = "10.3951")
  private double longitude;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Schema(description = "Optional capacity of the position type", example = "100")
  private Integer capacity;

  @NotNull(message = "Type ID cannot be null")
  @Schema(description = "Type ID of the position type", example = "1")
  private Long typeId;
}
