package stud.ntnu.no.krisefikser.dtos.map.shelters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO for requesting nearby shelters, includes user's position and search radius.
 * <p>
 * This class is used to encapsulate the data required to find shelters within a specified radius
 * from a user's location.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO for requesting nearby shelters based on user location and radius.")
public class ShelterRequest {

  @Schema(description = "Latitude of the user", example = "63.4305")
  private Double latitude;

  @Schema(description = "Longitude of the user", example = "10.3951")
  private Double longitude;

  @Schema(description = "Search radius in meters", example = "5000")
  private Double radius;
}
