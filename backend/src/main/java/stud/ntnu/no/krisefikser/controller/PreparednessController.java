package stud.ntnu.no.krisefikser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.krisefikser.dtos.preparedness.PreparednessSummary;
import stud.ntnu.no.krisefikser.service.PreparednessService;

/**
 * Controller for accessing household preparedness information.
 */
@RestController
@RequestMapping("/api/preparedness")
@RequiredArgsConstructor
@Tag(name = "Preparedness", description = "Endpoints for accessing household preparedness information")
public class PreparednessController {
  private static final Logger logger = LogManager.getLogger(PreparednessController.class);
  private final PreparednessService preparednessService;

  /**
   * Retrieves the preparedness summary for a specific household.
   *
   * @param householdId the ID of the household
   * @return a PreparednessSummary object containing preparedness metrics
   */
  @Operation(summary = "Get preparedness summary for a household",
      description = "Retrieves a summary of household preparedness including scores and item statistics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved preparedness summary"),
      @ApiResponse(responseCode = "404", description = "Household not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/{householdId}")
  public PreparednessSummary getHouseholdPreparednessSummary(@PathVariable Long householdId) {
    logger.info("Received request for preparedness summary for household ID: {}", householdId);
    PreparednessSummary summary = preparednessService.calculateHouseholdPreparednessSummary(householdId);
    logger.info("Returning preparedness summary for household ID: {}", householdId);
    return summary;
  }
}