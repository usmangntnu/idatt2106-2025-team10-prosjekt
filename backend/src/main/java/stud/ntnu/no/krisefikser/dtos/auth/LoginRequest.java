package stud.ntnu.no.krisefikser.dtos.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO for login request.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "Request object containing user credentials for authentication.")
public class LoginRequest {

  /**
   * The email of the user attempting to login.
   */
  @Schema(description = "The email of the user attempting to log in", example = "johndoe@example.com")
  private String email;

  /**
   * The username of the user attempting to login.
   */
  @Schema(description = "The username of the user attempting to log in", example = "johndoe")
  private String username;

  /**
   * The password of the user attempting to login.
   */
  @NotBlank(message = "Password cannot be blank")
  @Schema(description = "The password of the user attempting to log in", example = "password123")
  private String password;
}
