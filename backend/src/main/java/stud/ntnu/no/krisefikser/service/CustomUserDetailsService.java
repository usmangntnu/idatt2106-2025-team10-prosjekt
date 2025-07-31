package stud.ntnu.no.krisefikser.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.UserRepository;

import static stud.ntnu.no.krisefikser.exception.CustomErrorMessage.USER_NOT_FOUND;

/**
 * Service implementation for loading user details from the database.
 * <p>
 * Implements {@link UserDetailsService} to provide authentication functionality
 * required by Spring Security.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

  private final UserRepository userRepository;
  /**
   * Loads a user by their username.
   * <p>
   * This method is used by Spring Security during authentication.
   * </p>
   *
   * @param identity the email or username of the user to be loaded
   * @return the {@link UserDetails} of the authenticated user
   * @throws AppEntityNotFoundException if the user is not found in the database
   */
  @Override
  public UserDetails loadUserByUsername(String identity) throws AppEntityNotFoundException { //TODO: endre exception
    try {
      logger.info("Attempting to load user by identity: {}", identity);
      return userRepository.findByEmail(identity)
              .or(() -> userRepository.findByUsername(identity))
              .orElseThrow(() -> {
            logger.warn("User not found with identity: {}", identity);
            return new AppEntityNotFoundException(USER_NOT_FOUND);
          });
    } catch(Exception e) {
      logger.error("Error loading user by email: {}", identity, e);
      throw new AppEntityNotFoundException(USER_NOT_FOUND);
    }
  }
}