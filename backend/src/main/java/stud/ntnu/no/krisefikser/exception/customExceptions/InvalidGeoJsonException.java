package stud.ntnu.no.krisefikser.exception.customExceptions;

import lombok.Getter;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;

/**
 * Exception thrown when an invalid GeoJSON string is encountered.
  * <p>
 *  This exception is typically thrown when the GeoJSON string cannot be parsed, for instance,
 *  when the mapper encounters an invalid format or structure from the client request.
 *  </p>
 */
@Getter
public class InvalidGeoJsonException extends RuntimeException {

  /**
   * Custom error message containing details about the exception.
   */
  private final CustomErrorMessage errorMessage;

  /**
   * Constructs a new {@code InvalidGeoJsonException} with the specified error message.
   *
   * @param errorMessage the detailed custom error message
   */
  public InvalidGeoJsonException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
}
  }

