package stud.ntnu.no.krisefikser.dtos.map.position;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO representing the response for a position, including metadata like
 * <p>
 *   This is a Data Transfer Object (DTO) used for transferring position data
 *   between the server and client.
 * </p>
 */
@Data
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO representing the response for a position, including metadata like " +
    "latitude, longitude, and type.")
public class PositionResponse {

  @Schema(description = "ID of the position", example = "1")
  private Long id;

  @Schema(description = "Title of the position", example = "Food-Shelter in Oslo")
  private String title;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Schema(description = "Description of the position", example = "A food-shelter with a capacity of 100 people")
  private String description;

  @Schema(description = "Latitude of the position", example = "63.4305")
  private double latitude;

  @Schema(description = "Longitude of the position", example = "10.3951")
  private double longitude;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Schema(description = "Optional capacity of the position", example = "100")
  private Integer capacity;

  @Schema(description = "Type of the position", example = "Shelter")
  private PositionTypeResponse type;
}
