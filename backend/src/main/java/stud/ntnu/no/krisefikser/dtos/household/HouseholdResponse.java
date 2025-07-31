package stud.ntnu.no.krisefikser.dtos.household;

import lombok.Data;
import lombok.experimental.Accessors;
import stud.ntnu.no.krisefikser.dtos.user.UserResponse;

import java.util.List;

@Data
@Accessors(chain = true)
/**
 * DTO for household response.
 */
public class HouseholdResponse {
    private Long id;
    private String householdName;
    private String address;
    private String postalCode;
    private String city;
    private double latitude;
    private double longitude;
    private List<UserResponse> users;
    private UserResponse owner;
}
