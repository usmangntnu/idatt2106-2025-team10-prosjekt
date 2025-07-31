package stud.ntnu.no.krisefikser.dtos.map.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import stud.ntnu.no.krisefikser.entities.map.EventSeverity;
import stud.ntnu.no.krisefikser.entities.map.EventStatus;

/**
 * DTO representing a request to create or update an event.
 * <p>
 *   This class is used to encapsulate the data required for creating or updating an event,
 *   including the title, description, geometry, event type ID, and severity.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO for creating or updating an event, including title, description, " +
    "geometry, event type ID, and severity.")
public class EventRequest {

  @NotBlank(message = "Title must not be blank")
  @Schema(description = "Title of the event", example = "Earthquake in Oslo")
  private String title;

  @Schema(description = "Detailed description of the event",
      example = "A magnitude 5.0 earthquake was reported in Oslo.")
  private String description;

  @Schema(description = "Geometry of the event in GeoJSON format",
      example = "{\"type\": \"Point\", \"coordinates\": [10.3951, 63.4305]}")
  private String geometryGeoJson;

  @NotNull(message = "Event type ID must not be null")
  @Schema(description = "ID of the event type", example = "1")
  private Long typeId;

  @NotNull(message = "Event status must not be null")
  @Schema(description = "Status of the event", example = "ACTIVE")
  private EventStatus status;

  @NotNull(message = "Start date and time must not be null")
  @Schema(description = "Start date and time of the event", example = "2023-10-01T12:00:00Z")
  private String startTime;

  @NotNull(message = "Severity must not be null")
  @Schema(description = "Severity of the event", example = "HIGH")
  private EventSeverity severity;

  @Schema(description = "Circle data dto if the event is a circle",
      example = "Circle data in GeoJSON format")
  private CircleDataDto circleData;
}
