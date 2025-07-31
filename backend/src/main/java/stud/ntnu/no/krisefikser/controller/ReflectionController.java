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
import stud.ntnu.no.krisefikser.dtos.reflection.ReflectionNoteRequest;
import stud.ntnu.no.krisefikser.dtos.reflection.ReflectionNoteResponse;
import stud.ntnu.no.krisefikser.service.ReflectionService;

import java.util.List;

/**
 * Controller for managing reflections.
 */
@RestController
@RequestMapping("/api/reflection")
@RequiredArgsConstructor
@Tag(name = "Reflections", description = "Endpoints for fetching and generating new reflection notes")
public class ReflectionController {
  private static final Logger logger = LogManager.getLogger(HouseholdController.class);
  private final ReflectionService reflectionService;

  /**
   * Adds a reflection note for the current user.
   *
   * @param reflectionNoteRequest the request containing the reflection note details
   * @return a response entity indicating the result of the operation
   */
  @Operation(summary = "Add a reflection note",
      description = "Adds a reflection note for the current user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Reflection note added successfully"),
      @ApiResponse(responseCode = "403", description = "No user logged in")
  })
  @PostMapping("/add")
  public ResponseEntity<?> addReflectionNote(@RequestBody ReflectionNoteRequest reflectionNoteRequest) {
    logger.info("Adding reflection note");
    reflectionService.addReflectionNote(reflectionNoteRequest);
    logger.info("Reflection note added successfully");
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves all public reflection notes that are not created by the current user.
   *
   * @return a list of public reflection notes
   */
  @Operation(summary = "Get all public reflection notes",
      description = "Retrieves all public reflection notes that are not created by the current user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Public reflection notes retrieved successfully"),
      @ApiResponse(responseCode = "403", description = "No user logged in")
  })
  @GetMapping("/public")
  public ResponseEntity<List<ReflectionNoteResponse>> getAllPublicReflectionNotes() {
    logger.info("Fetching all public reflection notes");
    List<ReflectionNoteResponse> reflectionNotes = reflectionService.getAllPublicReflectionNotes();
    logger.info("Fetched {} public reflection notes", reflectionNotes.size());
    return ResponseEntity.ok(reflectionNotes);
  }

  /**
   * Retrieves all reflection notes created by the current user.
   *
   * @return a list of reflection notes created by the current user
   */
  @Operation(summary = "Get all my reflection notes",
      description = "Retrieves all reflection notes created by the current user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "My reflection notes retrieved successfully"),
      @ApiResponse(responseCode = "403", description = "No user logged in")
  })
  @GetMapping("/my")
  public ResponseEntity<List<ReflectionNoteResponse>> getMyReflectionNotes() {
    logger.info("Fetching all reflection notes for the current user");
    List<ReflectionNoteResponse> reflectionNotes = reflectionService.getAllMyReflectionNotes();
    logger.info("Fetched {} reflection notes for the current user", reflectionNotes.size());
    return ResponseEntity.ok(reflectionNotes);
  }

  /**
   * Retrieves all reflection notes in the user's household.
   *
   * @return a list of reflection notes in the user's household
   */
  @Operation(summary = "Get all household reflection notes",
      description = "Retrieves all reflection notes in the user's household")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Household reflection notes retrieved successfully"),
      @ApiResponse(responseCode = "403", description = "No user logged in"),
      @ApiResponse(responseCode = "404", description = "User not in a household")
  })
  @GetMapping("/household")
  public ResponseEntity<List<ReflectionNoteResponse>> getHouseholdReflectionNotes() {
    logger.info("Fetching all household reflection notes");
    List<ReflectionNoteResponse> reflectionNotes = reflectionService.getAllReflectionNotesInMyHousehold();
    logger.info("Fetched {} household reflection notes", reflectionNotes.size());
    return ResponseEntity.ok(reflectionNotes);
  }

  /**
   * Deletes a reflection note by its ID.
   *
   * @param id the ID of the reflection note to delete
   * @return a response entity indicating the result of the operation
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a reflection note",
      description = "Deletes a reflection note by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Reflection note deleted successfully"),
      @ApiResponse(responseCode = "403", description = "No user logged in or the user is not the owner of the reflection note"),
      @ApiResponse(responseCode = "404", description = "Reflection note not found")
  })
  public ResponseEntity<?> deleteReflectionNote(@PathVariable Long id) {
    logger.info("Deleting reflection note with ID: {}", id);
    reflectionService.deleteReflectionNoteById(id);
    logger.info("Reflection note with ID {} deleted successfully", id);
    return ResponseEntity.ok().build();
  }


}
