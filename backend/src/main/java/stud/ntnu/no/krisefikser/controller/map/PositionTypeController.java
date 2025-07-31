package stud.ntnu.no.krisefikser.controller.map;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionTypeResponse;
import stud.ntnu.no.krisefikser.service.PositionTypeService;

import java.util.List;

/**
 * Controller for managing position types in the system.
 * <p>
 * This class is responsible for handling requests related to position types,
 * such as retrieving all position types,
 * creating new position types, and updating existing ones.
 * </p>
 */
@RestController
@RequestMapping("/api/position-types")
@RequiredArgsConstructor
@Tag(name = "Position Types", description = "Endpoints for managing position types in the system")
public class PositionTypeController {

  private static final Logger logger = LogManager.getLogger(PositionTypeController.class);
  private final PositionTypeService positionTypeService;

  /**
   * Method for retrieving all position types.
   *
   * @return a response entity containing a list of all position types
   */
@Operation(summary = "Gets all position types",
    description = "Fetches all position types from the database.")
  @GetMapping
  public ResponseEntity<List<PositionTypeResponse>> getAllPositionTypes() {
    logger.info("Fetching all position types");
    List<PositionTypeResponse> positionTypes = positionTypeService.getAllPositionTypes();
    logger.info("Retrieved {} position types", positionTypes.size());
    return ResponseEntity.ok(positionTypes);
  }

}
