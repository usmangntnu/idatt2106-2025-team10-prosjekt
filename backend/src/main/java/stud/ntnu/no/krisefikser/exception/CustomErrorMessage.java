package stud.ntnu.no.krisefikser.exception;

import lombok.Getter;

/**
 * Enum representing custom error messages and their associated HTTP status
 * codes.
 * <p>
 * Used throughout the application to provide consistent error handling and
 * responses.
 * Each constant defines both a status code and a descriptive message.
 * </p>
 */
@Getter
public enum CustomErrorMessage {

  /**
   * Error when a notification is not found.
   */
  NOTIFICATION_NOT_FOUND(404, "Notification not found."),
  /**
   * Error when the request is invalid.
   */
  REFLECTION_NOTE_NOT_FOUND(404, "Reflection note not found."),

  /**
   * Error when the request is invalid.
   */
  HOUSEHOLD_NOT_FOUND(404, "Household not found."),

  /**
   * Error when the request is invalid.
   */
  USER_NOT_ENABLED(403, "User is not enabled."),
  /**
   * Error when the request is invalid.
   */
  NO_USER_LOGGED_IN(401, "No user is logged in."),
  /**
   * Error when the request is invalid.
   */
  NO_TOKEN_FOUND(404, "No token found."),
  /**
   * Error when the request is invalid.
   */
  USER_ALREADY_IN_HOUSEHOLD(409, "User is already in the household."),
  /**
   * Error when the request email is not found.
   */
  EMAIL_NOT_FOUND(404, "Email not found."),
  /**
   * Error when the request is invalid.
   */
  USER_NOT_IN_HOUSEHOLD(404, "User is not in a household."),
  /**
   * Error when the request is invalid.
   */
  UNAUTHORIZED_OPERATION(403, "You are not authorized to perform this operation."),

  /**
   * Error when the request is invalid.
   */
  USER_NOT_FOUND(404, "User not found."),
  /**
   * Error when the request is invalid.
   */
  EMAIL_ALREADY_EXISTS  (404, "Email already exists"),

  /**
   * Error when the quiz attempt is not found.
   */
  QUIZ_ATTEMPT_NOT_FOUND(404, "Quiz attempt not found."),

  /**
   * Error when the quiz attempt answer is not found.
   */
  QUIZ_ATTEMPT_ANSWER_NOT_FOUND(404, "Quiz attempt answer not found."),

  /**
   * Error when the answer option is not found.
   */
  ANSWER_OPTION_NOT_FOUND(404, "Answer option not found."),

  /**
   * Error when the correct answer is not found for a given question.
   */
  CORRECT_ANSWER_NOT_FOUND(500, "Correct answer option not found for question."),

  /**
   * Error when the quiz question limit is exceeded.
   */
  QUIZ_QUESTION_LIMIT_EXCEEDED(400, "Quiz question limit exceeded."),

  /**
   * Error when creating a duplicate ItemCategory.
   */
  ITEM_CATEGORY_ALREADY_EXISTS(409, "ItemCategory already exists."),

  /**
   * Error when the verification token isn't found.
   */
  TOKEN_NOT_FOUND(404, "Token not found."),

  /**
   * Error when the item category isn't found
   */
  ITEM_CATEGORY_NOT_FOUND(404, "Item category not found."),

  /**
   * Error when the StorageItem isn't found
   */
  STORAGE_ITEM_NOT_FOUND(404, "Storage item not found."),

  /**
   * Error when the qustion is already answered.
   */
  QUESTION_ALREADY_ANSWERED(409, "Question already answered."),

  
  /**
   * Error when the answer is invalid for the question.
   */
  INVALID_ANSWER_FOR_QUESTION(400, "Invalid answer for question."),

  // --- Generic ---
  /**
   * Error when an internal server error occurs.
   */
  INTERNAL_SERVER_ERROR(500, "An internal server error occurred."),

  /**
   * Event not found.
   */
  EVENT_NOT_FOUND(404, "Event not found."),

  /**
   * Event type not found.
   */
  EVENT_TYPE_NOT_FOUND(404, "Event type not found."),

  /**
   * Event type already exists.
   */
  EVENT_TYPE_ALREADY_EXISTS(409, "Event type already exists."),

  /**
   * Error when GeoJson is not valid.
   */
  GEOJSON_NOT_VALID(400, "GeoJson data is not valid."),

  /**
   * Error when the position type is not found.
   */
  POSITION_TYPE_NOT_FOUND(404, "Position type not found."),

  /**
   * Error when the position already exists.
   */
  POSITION_ALREADY_EXISTS(409, "Position already exists."),

  /**
   * Error when the position is not found.
   */
  POSITION_NOT_FOUND(404, "Position not found.");



  /**
   * The HTTP status code associated with the error.
   */
  private final int status;

  /**
   * The descriptive error message.
   */
  private final String message;

  /**
   * Constructor for CustomErrorMessage.
   *
   * @param status  the HTTP status code
   * @param message the descriptive error message
   */
  CustomErrorMessage(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
