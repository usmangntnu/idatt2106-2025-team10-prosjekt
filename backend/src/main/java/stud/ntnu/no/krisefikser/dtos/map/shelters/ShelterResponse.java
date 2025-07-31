package stud.ntnu.no.krisefikser.dtos.map.shelters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO for returning shelter information to the frontend.
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO representing a shelter, including location and capacity.")
public class ShelterResponse {

  @Schema(description = "Local unique ID for the shelter",
      example = "07db21b5-1dcf-43c2-8a76-a85ba4d5d055")
  private String localId;

  @Schema(description = "Address of the shelter", example = "Vognmannsgt. 1 - Trygdegården")
  private String address;

  @Schema(description = "Capacity of the shelter (number of people)", example = "215")
  private int capacity;

  @Schema(description = "Latitude of the shelter", example = "59.7438")
  private double latitude;

  @Schema(description = "Longitude of the shelter", example = "10.6791")
  private double longitude;

}
