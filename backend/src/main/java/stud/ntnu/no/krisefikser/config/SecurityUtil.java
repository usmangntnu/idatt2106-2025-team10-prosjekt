package stud.ntnu.no.krisefikser.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.UnauthorizedOperationException;
import stud.ntnu.no.krisefikser.repository.UserRepository;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
  private final UserRepository userRepository;

  public Optional<User> getCurrentUserIfAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null ||
            !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
      return Optional.empty();
    }

    String identity = authentication.getName();

    // Gets user either by username (admin) or email (user)
    return userRepository.findByUsername(identity)
            .or(() -> userRepository.findByEmail(identity));
  }

  /**
   * Retrieves the currently authenticated user from the security context.
   *
   * @return the authenticated {@link User}
   * @throws RuntimeException if the user is not found in the repository
   */
  public User getCurrentUser() {
    return getCurrentUserIfAuthenticated()
            .orElseThrow(() -> new UnauthorizedOperationException(CustomErrorMessage.UNAUTHORIZED_OPERATION));
  }


  /**
   * Creates a secure cookie with the provided JWT token.
   *
   * @param token the JWT token
   * @return the response cookie
   */
  public ResponseCookie createCookie(String token) {
    return ResponseCookie.from("auth-token", token)
            .httpOnly(true)
            .secure(false) // kun for lokal dev (ikke prod!)
            .sameSite("Lax")
            .path("/")
            .maxAge(Duration.ofDays(365).toSeconds())
            .build();
    //TODO add this when https is configured '.secure(true)'
  }

  /**
   * Creates a cookie to invalidate the JWT token (logout).
   *
   * @return the response cookie for logout
   */
  public ResponseCookie createLogoutCookie() {
    return ResponseCookie.from("auth-token", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ZERO)
            .build();
  }
}
