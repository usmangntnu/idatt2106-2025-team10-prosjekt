package stud.ntnu.no.krisefikser.dtos.map.position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO for representing a PositionType in API responses.
 * <p>
 *   This class is used to encapsulate the data required to represent a position type in
 *   API responses.
 *  </p>
 */
@Data
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO for representing a PositionType in API responses. " +
    "Contains title, description and identifier.")
public class PositionTypeResponse {

  @Schema(description = "ID of the position type", example = "1")
  private Long id;

  @Schema(description = "Name of the position type", example = "Shelter")
  private String name;

  @Schema(description = "Description of the position type", example = "A place providing shelter.")
  private String description;

  @Schema(description = "Icon URL for the position type", example = "https://example.com/icon.png")
  private String iconPath;
}
