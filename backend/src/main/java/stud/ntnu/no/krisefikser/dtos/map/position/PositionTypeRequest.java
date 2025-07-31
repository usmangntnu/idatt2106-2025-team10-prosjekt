package stud.ntnu.no.krisefikser.dtos.map.position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO representing the request for a position type.
 * <p>
 *   This class is used to encapsulate the data required to represent a position type in
 *   API requests.
 *  </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO representing the request for a position type.")
public class PositionTypeRequest {

  @Schema(description = "Name of the position type", example = "Shelter")
  private String name;

  @Schema(description = "Description of the position type", example = "A place providing shelter.")
  private String description;

}
