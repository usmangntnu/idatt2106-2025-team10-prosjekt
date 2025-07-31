package stud.ntnu.no.krisefikser.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.krisefikser.dtos.user.UserResponse;
import stud.ntnu.no.krisefikser.service.UserService;

/**
 * Controller for handling user-related operations.
 *
 * <p>Provides endpoints for profile management, managing favorites, and retrieving user-specific data.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing user profiles, favorites, and personal listings")
public class UserController {

  private static final Logger logger = LogManager.getLogger(UserController.class);

  private final UserService userService;

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
  @GetMapping("/profile")
  public ResponseEntity<UserResponse> getCurrentUserIfAuthenticated() {
    logger.info("Fetching current user profile");
    UserResponse userResponse = userService.getCurrentUser();
    logger.info("Fetched profile for user '{}'", userResponse.getUsername() != null ? userResponse.getUsername() : userResponse.getEmail());
    return ResponseEntity.ok(userResponse);
  }
}
