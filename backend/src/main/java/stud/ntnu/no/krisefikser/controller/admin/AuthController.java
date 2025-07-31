/* package stud.ntnu.no.krisefikser.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.krisefikser.service.AdminService;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Tag(title = "Authentications", description = "Endpoints for admin registration and login")
public class AuthController {
  private static final Logger logger = LogManager.getLogger(AuthController.class);
  private final AdminService adminService;

  /**
   * Authenticate and return JWT token as cookie.
   *
   * @param loginRequest the login request containing admin username and password
   * @param response    the HTTP response to set the cookie
   * @return a response entity with the authentication status and JWT token as a cookie
   */

/*
  @Operation(summary = "Login", description = "Authenticates admin credentials and returns a JWT token as cookie if valid")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Login successful"),
          @ApiResponse(responseCode = "401", description = "Invalid username or password, or admin is not valid"),
          @ApiResponse(responseCode = "500", description = "Invalid signing key for signing JWT token")
  })
  @PostMapping("/login")
  public ResponseEntity<String> authenticate(@RequestBody @Validated AdminLoginRequest loginRequest, HttpServletResponse response) {
    logger.info("Auth: Authenticating user with username '{}'", loginRequest.getUsername());
    ResponseCookie cookie = adminService.authenticateAndGetCookie(loginRequest);
    logger.debug("Auth: JWT token created for username '{}': {}", loginRequest.getUsername(), cookie.getValue());
    response.addHeader("Set-Cookie", cookie.toString());
    logger.info("Auth: JWT token set as cookie for username '{}'", loginRequest.getUsername());
    return ResponseEntity.ok("Login successful");
  }
}

*/