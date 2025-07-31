package stud.ntnu.no.krisefikser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.krisefikser.dtos.preparedness.NotificationRequest;
import stud.ntnu.no.krisefikser.dtos.preparedness.NotificationResponse;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.NotificationNotFoundException;
import stud.ntnu.no.krisefikser.service.NotificationService;

import java.util.List;

/**
 * Controller for managing notifications.
 * <p>
 * This controller provides endpoints to create, delete, and retrieve notifications
 * related to items in the system.
 * </p>
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Endpoints for managing notifications")
public class NotificationController {
  private static final Logger logger = LogManager.getLogger(NotificationController.class);

  private final NotificationService notificationService;
  private final SimpMessagingTemplate messagingTemplate;


  @Operation(summary = "Get notifications for a household",
      description = "Retrieves all notifications for a specific household")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications"),
      @ApiResponse(responseCode = "404", description = "Household not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/household/{householdId}")
  public ResponseEntity<List<NotificationResponse>> getNotificationsForHousehold(@PathVariable Long householdId) {
    logger.info("Received request for notifications for household ID: {}", householdId);
    List<NotificationResponse> notifications = notificationService.getNotificationsForHousehold(householdId);
    return ResponseEntity.ok(notifications);
  }

  @Operation(summary = "Create a notification",
      description = "Creates a new notification")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully created notification"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "404", description = "Household or storage item not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
    logger.info("Received request to create notification for household ID: {}", request.getHouseholdId());
    NotificationResponse response = notificationService.createNotification(request);
    messagingTemplate.convertAndSend("/topic/notifications", response);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Delete a notification",
      description = "Deletes a notification by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully deleted notification"),
      @ApiResponse(responseCode = "404", description = "Notification not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
    logger.info("Received request to delete notification with ID: {}", id);
    notificationService.deleteNotification(id);
    messagingTemplate.convertAndSend("/topic/notifications/delete", id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Handles exceptions when a requested entity is not found.
   *
   * @param ex the exception
   * @return error response with 404 status
   */
  @ExceptionHandler(AppEntityNotFoundException.class)
  public ResponseEntity<Object> handleEntityNotFound(AppEntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getErrorMessage());
  }

  /**
   * Handles exceptions when a notification is not found.
   *
   * @param ex the exception
   * @return error response with 404 status
   */
  @ExceptionHandler(NotificationNotFoundException.class)
  public ResponseEntity<Object> handleNotificationNotFound(NotificationNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getErrorMessage());
  }

  /**
   * Handles validation exceptions.
   *
   * @param ex the exception
   * @return error response with 400 status
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
}