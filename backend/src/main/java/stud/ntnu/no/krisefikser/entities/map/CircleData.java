package stud.ntnu.no.krisefikser.entities.map;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents the data for a circle on a map.
 */
@Getter
@Setter
@Accessors(chain = true)
@Embeddable
public class CircleData {

  @Column(name = "circle_radius")
  private Double radius;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "latitude")
  private Double latitude;


}


