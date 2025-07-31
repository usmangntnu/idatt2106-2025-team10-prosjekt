package stud.ntnu.no.krisefikser.dtos.household;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
/**
 * DTO for creating a household.
 */
public class HouseholdRequest {
  private String name;
  private String address;
  private String postalCode;
  private String city;
  private double latitude;
  private double longitude;
}
