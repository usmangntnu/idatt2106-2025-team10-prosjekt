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
import stud.ntnu.no.krisefikser.dtos.household.HouseholdRequest;
import stud.ntnu.no.krisefikser.dtos.household.HouseholdResponse;
import stud.ntnu.no.krisefikser.service.HouseholdService;

/**
 * Controller for handling household-related endpoints.
 */
@RestController
@RequestMapping("/api/household")
@RequiredArgsConstructor
@Tag(name = "Household", description = "Endpoints for user joining a household or creating a new one")
public class HouseholdController {
  private static final Logger logger = LogManager.getLogger(HouseholdController.class);
  private final HouseholdService householdService;

  /**
   * Join an existing household.
   *
   * @param householdToken the token for the household to join
   *
   * @return a response entity with the status of the operation
   */
  @Operation(summary = "Join existing household", description = "Joins an existing household using the provided token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully joined household"),
      @ApiResponse(responseCode = "404", description = "Household not found"),
      @ApiResponse(responseCode = "409", description = "User already belongs to a household")
  })
  @PostMapping("/join/{householdToken}")
  public ResponseEntity<String> joinExistingHousehold(@PathVariable String householdToken){
    logger.info("Joining existing household with token '{}'", householdToken);
    householdService.joinHousehold(householdToken);
    logger.info("Successfully joined household with token '{}'", householdToken);
    return ResponseEntity.ok("Successfully joined household");
  }

  /**
   * Create a new household.
   *
   * @param householdRequest the request containing household details
   * @return a response entity with the status of the operation
   */
  @Operation(summary = "Create new household", description = "Creates a new household and adds the current user to it")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Household created successfully"),
      @ApiResponse(responseCode = "404", description = "Invalid email"),
      @ApiResponse(responseCode = "409", description = "User already belongs to a household")
  })
  @PostMapping("/create")
  public ResponseEntity<?> createNewHousehold(@RequestBody HouseholdRequest householdRequest) {
    logger.info("Creating new household");
    householdService.createNewHousehold(householdRequest);
    logger.info("Household created successfully");
    return ResponseEntity.ok(householdRequest.getName());
  }

  /**
   * Invite a user to join the household.
   *
   * @return a response entity with the generated invite token
   */
  @Operation(summary = "Invite user to household", description = "Creates an invite token for a user to join the current user's household")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Invite token created successfully"),
      @ApiResponse(responseCode = "401", description = "No user logged in"),
  })
  @PostMapping("/invite")
  public ResponseEntity<String> inviteUserToHousehold(){
    logger.info("Creating invite token");
    String token = householdService.createInviteToken();
    logger.info("Invite token created successfully");
    return ResponseEntity.ok(token);
  }

  /**
   * Get the current user's household.
   *
   * @return a response entity with the household details
   */
  @Operation(summary = "Get current user's household", description = "Fetches the current user's household details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Household fetched successfully"),
      @ApiResponse(responseCode = "404", description = "User not found or user does not have a household"),
  })
  @GetMapping("/my")
  public ResponseEntity<HouseholdResponse> getMyHousehold() {
    logger.info("Fetching current user's household");
    HouseholdResponse householdResponse = householdService.getMyHousehold();
    logger.info("Fetched current user's household successfully");
    return ResponseEntity.ok(householdResponse);
  }

  /**
   * Leave the current user's household.
   *
   * @return a response entity with the status of the operation
   */
  @Operation(summary = "Leave household", description = "Leaves the current user's household")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully left household"),
      @ApiResponse(responseCode = "404", description = "User not found or user does not have a household"),
      @ApiResponse(responseCode = "409", description = "User is not the owner of the household but is till the only user in the household")
  })
  @PostMapping("/leave")
  public ResponseEntity<?> leaveMyHousehold() {
    logger.info("Leaving household");
    householdService.leaveHousehold();
    logger.info("Left household successfully");
    return ResponseEntity.ok("Left household successfully");
  }


}
