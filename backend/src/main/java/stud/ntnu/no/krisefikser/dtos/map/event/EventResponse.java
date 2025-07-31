package stud.ntnu.no.krisefikser.dtos.map.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import stud.ntnu.no.krisefikser.entities.map.EventSeverity;
import stud.ntnu.no.krisefikser.entities.map.EventStatus;

/**
 * DTO representing the response for an event, including metadata like title, geometry, type title,
 * and severity.
 * <p>
 *   This class is used to encapsulate the data returned when querying for an event,
 *   including the event's ID, title, description, geometry, event type, severity, start time,
 *   status, and circle data if applicable.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO representing the response for an event, including metadata like " +
    "title, geometry, type title, and severity.")
public class EventResponse {

  @Schema(description = "ID of the event", example = "1")
  private Long id;

  @Schema(description = "Title of the event", example = "Earthquake in Oslo")
  private String title;

  @Schema(description = "Detailed description of the event", example = "A magnitude 5.0 earth-" +
      "quake was reported in Oslo.")
  private String description;

  @Schema(description = "Geometry of the event in GeoJSON format",
      example = "{\"type\": \"Point\", \"coordinates\": [10.3951, 63.4305]}")
  private String geometryGeoJson;

  @Schema(description = "EventType of the event", example = "Natural Disaster")
  private EventTypeResponse eventType;

  @Schema(description = "Severity of the event", example = "HIGH")
  private EventSeverity severity;

  @NotNull(message = "Start date and time must not be null")
  @Schema(description = "Start date and time of the event", example = "2023-10-01T12:00:00Z")
  private String startTime;

  @NotNull(message = "Event status must not be null")
  @Schema(description = "Status of the event", example = "ACTIVE")
  private EventStatus status;

  @Schema(description = "Circle data dto if the event is a circle",
      example = "Circle data in GeoJSON format")
  private CircleDataDto circleData;
}
