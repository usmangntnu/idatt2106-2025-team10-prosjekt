package stud.ntnu.no.krisefikser.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing detailed user information.
 * <p>
 * Used for returning full user details in API responses.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "DTO representing detailed user information")
public class UserResponse {

  /**
   * The unique identifier of the user.
   */
  @Schema(description = "Unique identifier of the user", example = "1")
  private Long id;

  /**
   * The username of the user.
   */
  @Schema(description = "Username of the user", example = "john_doe")
  private String username;

  /**
   * The email of the user.
   */
  @Schema(description = "Email of the user", example = "john@gmail.com")
  private String email;

  private String firstName;
  private String lastName;


  /**
   * List with roles associated with the user
   */
  @Schema(description = "Roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
  private List<String> roles;

  private boolean isHouseholdAdmin;
}
