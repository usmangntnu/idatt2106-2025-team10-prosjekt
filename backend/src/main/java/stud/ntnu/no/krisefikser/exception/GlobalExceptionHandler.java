package stud.ntnu.no.krisefikser.exception;

import io.jsonwebtoken.security.InvalidKeyException;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import stud.ntnu.no.krisefikser.exception.customExceptions.*;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Global exception handler for handling various exceptions across the application.
 * <p>
 * Provides centralized exception handling using {@link RestControllerAdvice}.
 * Ensures that all errors are returned with a consistent structure.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Creates an error response entity for exceptions with a predefined {@link CustomErrorMessage}.
   *
   * @param errorMessage the predefined custom error message
   * @param e            the thrown exception
   * @param request      the current web request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  private ResponseEntity<ErrorDetail> createErrorResponseEntity(CustomErrorMessage errorMessage, Exception e, WebRequest request) {
    ErrorDetail errorDetail = new ErrorDetail(
        LocalDateTime.now(),
        errorMessage.getStatus(),
        HttpStatus.valueOf(errorMessage.getStatus()).getReasonPhrase(),
        e.getClass().getName(),
        errorMessage.getMessage(),
        request.getDescription(false),
        Arrays.toString(e.getStackTrace())
    );
    return new ResponseEntity<>(errorDetail, HttpStatus.valueOf(errorMessage.getStatus()));
  }

  /**
   * Creates an error response entity for generic exceptions with dynamic {@link HttpStatus}.
   *
   * @param status  the HTTP status to be returned
   * @param e       the thrown exception
   * @param request the current web request
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  private ResponseEntity<ErrorDetail> createErrorResponseEntity(HttpStatus status, Exception e, WebRequest request) {
    ErrorDetail error = new ErrorDetail(
        LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        e.getClass().getName(),
        e.getMessage(),
        request.getDescription(false),
        Arrays.toString(e.getStackTrace())
    );
    return new ResponseEntity<>(error, status);
  }

  /**
   * Handles {@link AccessDeniedException} when access is forbidden.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorDetail> handleAccessDeniedException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.FORBIDDEN, e, request);
  }

  /**
   * Handles {@link AuthenticationException} for authentication failures.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorDetail> handleAuthenticationException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.BAD_REQUEST, e, request);
  }

  /**
   * Handles {@link InvalidKeyException} for invalid JWT signing keys.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(InvalidKeyException.class)
  public ResponseEntity<ErrorDetail> handleInvalidKeyException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
  }

  /**
   * Handles {@link IllegalArgumentException} for invalid method arguments.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorDetail> handleIllegalArgumentException(@NonNull IllegalArgumentException e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.BAD_REQUEST, e, request);
  }

  /**
   * Handles any unhandled exceptions.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDetail> handleGenericException(@NonNull Exception e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
  }

  /**
   * Handles {@link EditedValueUnchangedException} when edit operations do not result in any changes.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(EditedValueUnchangedException.class)
  public ResponseEntity<ErrorDetail> handleEditedValueUnchangedException(@NonNull EditedValueUnchangedException e, WebRequest request) {
    return createErrorResponseEntity(e.getErrorMessage(), e, request);
  }

  /**
   * Handles {@link EntityAlreadyExistsException} when attempting to create an entity that already exists.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(EntityAlreadyExistsException.class)
  public ResponseEntity<ErrorDetail> handleEntityAlreadyExistsException(@NonNull EntityAlreadyExistsException e, WebRequest request) {
    return createErrorResponseEntity(e.getErrorMessage(), e, request);
  }

  /**
   * Handles {@link AppEntityNotFoundException} when an entity cannot be found.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(AppEntityNotFoundException.class)
  public ResponseEntity<ErrorDetail> handleAppEntityNotFoundException(@NonNull AppEntityNotFoundException e, WebRequest request) {
    return createErrorResponseEntity(e.getErrorMessage(), e, request);
  }

  /**
   * Handles {@link EntityOperationException} when a generic operation on an entity fails.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(EntityOperationException.class)
  public ResponseEntity<ErrorDetail> handleEntityOperationException(@NonNull EntityOperationException e, WebRequest request) {
    return createErrorResponseEntity(e.getErrorMessage(), e, request);
  }

  /**
   * Handles {@link IllegalStateException} when an entity cannot be found.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorDetail> handleIllegalStateException(@NonNull IllegalStateException e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.CONFLICT, e, request);
  }

  /**
   * Handles {@link InvalidGeoJsonException} when the provided GeoJSON is invalid.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(InvalidGeoJsonException.class)
  public ResponseEntity<ErrorDetail>
  handleInvalidGeoJsonException(@NonNull InvalidGeoJsonException e, WebRequest request) {
    return createErrorResponseEntity(e.getErrorMessage(), e, request);
  }

  /**
   * Handles {@link MailException} when there is an error sending an email.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(MailException.class)
  public ResponseEntity<ErrorDetail> handleMailException(@NonNull MailException e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
  }

  /**
   * Handles {@link DisabledException} when a user is disabled.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ErrorDetail> handleDisabledException(@NonNull DisabledException e, WebRequest request) {
    return createErrorResponseEntity(HttpStatus.UNAUTHORIZED, e, request);
  }

  /**
   * Handles {@link UnauthorizedOperationException} when an unauthorized operation is attempted.
   *
   * @param e       the thrown exception
   * @param request the current web request
   *
   * @return a structured {@link ResponseEntity} containing {@link ErrorDetail}
   */
  @ExceptionHandler(UnauthorizedOperationException.class)
  public ResponseEntity<ErrorDetail> handleUnauthorizedOperationException(@NonNull UnauthorizedOperationException e, WebRequest request) {
    return createErrorResponseEntity(e.getErrorMessage(), e, request);
  }
}
