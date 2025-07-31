package stud.ntnu.no.krisefikser.exception.customExceptions;

import lombok.Getter;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;

/**
 * Exception thrown when a notification cannot be found in the system.
 * <p>
 * Used to indicate that a requested notification resource could not be located
 * in the database when performing operations like retrieval, update, or deletion.
 * Contains a {@link CustomErrorMessage} providing detailed error information.
 * </p>
 */
@Getter
public class NotificationNotFoundException extends RuntimeException {

  /**
   * Custom error message containing details about the exception.
   */
  private final CustomErrorMessage errorMessage;

  /**
   * Constructs a new {@code NotificationNotFoundException} with the specified error message.
   *
   * @param errorMessage the detailed custom error message
   */
  public NotificationNotFoundException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
  }
}