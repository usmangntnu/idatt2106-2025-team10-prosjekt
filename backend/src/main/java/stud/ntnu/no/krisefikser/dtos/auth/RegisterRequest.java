package stud.ntnu.no.krisefikser.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * DTO for register request.
 * <p>
 * Contains user credentials for registration.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "Request object containing user credentials for authentication.")
public class RegisterRequest {
  /**
   * The first title of the user attempting to register.
   */
  @NotBlank(message = "firstname cannot be blank")
  @Schema(description = "The firstname of the user attempting to log in", example = "john")
  private String firstName;

  /**
   * The last title of the user attempting to register.
   */
  @NotBlank(message = "lastname cannot be blank")
  @Schema(description = "The lastname of the user attempting to log in", example = "doe")
  private String lastName;

  /**
   * The email of the user attempting to register.
   */
  @NotBlank(message = "email cannot be blank")
  @Schema(description = "The email of the user attempting to log in", example = "johndoe@example.com")
  private String email;

  /**
   * The password of the user attempting to register.
   */
  @NotBlank(message = "Password cannot be blank")
  @Schema(description = "The password of the user attempting to log in", example = "password123")
  private String password;

  /**
   * The password captcha token for verifying the user.
   */
  @NotBlank(message = "Recaptcha token cannot be blank")
  @Schema(description = "The recaptcha token for verifying the user", example = "recaptcha-token")
  private String recaptchaToken;
}
