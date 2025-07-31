package stud.ntnu.no.krisefikser.dtos.map.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the circle data for an event, including center coordinates and radius.
 * <p>
 *   This class is used to encapsulate the data related to a circular area on a map,
 *   including the center coordinates (latitude and longitude) and the radius of the circle.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CircleDataDto {

  @NotNull(message = "Circle data must not be null")
  @Schema(description = "Radius of the circle in meters", example = "1000")
  private double radius;

  @NotNull(message = "Circle data must not be null")
  @Schema(description = "Center longitude of the circle", example = "10.3951")
  private double longitude;

  @NotNull(message = "Circle data must not be null")
  @Schema(description = "Center latitude of the circle", example = "63.4305")
  private double latitude;



}
