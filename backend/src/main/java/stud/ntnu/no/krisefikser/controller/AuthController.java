package stud.ntnu.no.krisefikser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.krisefikser.dtos.auth.LoginRequest;
import stud.ntnu.no.krisefikser.dtos.auth.RegisterRequest;
import stud.ntnu.no.krisefikser.dtos.user.UserResponse;

import stud.ntnu.no.krisefikser.service.UserService;

/**
 * Controller for handling authentication-related endpoints.
 *
 * <p>Provides endpoints for user registration and authentication.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentications", description = "Endpoints for user registration and login")
public class AuthController {
  private static final Logger logger = LogManager.getLogger(AuthController.class);
  private final UserService userService;


  /**
   * Register a new user.
   *
   * @param registerRequest the registration request containing user details.
   * @return a response entity with the registration status.
   */
  @Operation(summary = "Register new user", description = "Creates a new user account based on provided details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User registered successfully"),
      @ApiResponse(responseCode = "409", description = "User with the given email already exists")
  })
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest) {
    logger.info("Auth: Attempting to register user with email '{}'", registerRequest.getEmail());
    userService.register(registerRequest);
    logger.info("Auth: User registered successfully with email '{}'", registerRequest.getEmail());
    return ResponseEntity.ok("User registered successfully, please check your email to verify your account");
  }

  /**
   * Authenticate and return JWT token as cookie.
   *
   * @param loginRequest the login request containing email and password
   * @param response    the HTTP response to set the cookie
   * @return a response entity with the authentication status and JWT token as a cookie
   */
  @Operation(summary = "Login", description = "Authenticates user credentials and returns a JWT token as cookie if valid")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful"),
      @ApiResponse(responseCode = "401", description = "Invalid email or password, or user is not valid"),
      @ApiResponse(responseCode = "500", description = "Invalid signing key for signing JWT token")
  })
  @PostMapping("/login")
  public ResponseEntity<String> authenticate(@RequestBody @Validated LoginRequest loginRequest, HttpServletResponse response) {
    logger.info("Auth: Authenticating user with username '{}'", loginRequest.getEmail());
    ResponseCookie cookie = userService.authenticateAndGetCookie(loginRequest);
    logger.debug("Auth: JWT token created for username '{}': {}", loginRequest.getEmail(), cookie.getValue());
    response.addHeader("Set-Cookie", cookie.toString());
    logger.info("Auth: JWT token set as cookie for username '{}'", loginRequest.getEmail());
    return ResponseEntity.ok("Login successful");
  }

  /**
   * Logout the user by returning an invalid logout cookie.
   *
   * @param response the HTTP response to set the logout cookie
   * @return a response entity indicating successful logout
   */
  @Operation(summary = "Logout", description = "Logs out the user by invalidating the JWT token")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Logout successful"),
          @ApiResponse(responseCode = "500", description = "Invalid signing key for signing JWT token")
  })
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    logger.info("Auth: Logging out user");
    ResponseCookie logoutCookie = userService.createLogoutCookie();
    response.addHeader("Set-Cookie", logoutCookie.toString());
    logger.info("Auth: User logged out successfully");
    return ResponseEntity.ok().build();
  }

  /**
   * Verify the email address using the provided token.
   *
   * @param token the verification token
   * @param response the HTTP response to set the cookie
   * @return a response entity with the verification status
   */
  @Operation(summary = "Verify email", description = "Verifies the email address using the provided token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email verified successfully"),
      @ApiResponse(responseCode = "404", description = "Invalid token")
  })
  @GetMapping("/verify/{token}")
  public ResponseEntity<String> verifyEmail(@PathVariable String token, HttpServletResponse response) {
    logger.info("Auth: Verifying email with token '{}'", token);
    ResponseCookie cookie = userService.enableUser(token);
    logger.info("Auth: Email verified successfully with token '{}'", token);
    response.addHeader("Set-Cookie", cookie.toString());
    logger.info("Auth: JWT token set as cookie after email verification with token '{}'", token);
    return ResponseEntity.ok("Email verified successfully!");
  }

  /**
   * Retrieves the profile of the currently authenticated user.
   *
   * @return the profile of the logged-in user
   */
  @Operation(summary = "Get current user profile", description = "Fetches the profile of the currently authenticated user.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully fetched user profile"),
          @ApiResponse(responseCode = "401", description = "Unauthorized - User is not logged in")
  })
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser() {
    logger.info("Fetching current user profile");
    UserResponse userResponse = userService.getCurrentUser();
    if (userResponse == null) {
      logger.info("was null");
      return ResponseEntity.noContent().build();
    }
    logger.info("Fetched profile for user '{}'",
            userResponse.getUsername() != null ? userResponse.getUsername() : userResponse.getEmail());
    return ResponseEntity.ok(userResponse);
  }
}