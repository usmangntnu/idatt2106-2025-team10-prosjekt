package stud.ntnu.no.krisefikser.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.config.JWTUtil;
import stud.ntnu.no.krisefikser.config.SecurityUtil;
import stud.ntnu.no.krisefikser.dtos.auth.LoginRequest;
import stud.ntnu.no.krisefikser.dtos.auth.RegisterRequest;
import stud.ntnu.no.krisefikser.dtos.mappers.UserMapper;
import stud.ntnu.no.krisefikser.dtos.user.UserResponse;
import stud.ntnu.no.krisefikser.entities.Role;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.entities.UserRole;
import stud.ntnu.no.krisefikser.entities.VerificationToken;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.krisefikser.repository.UserRepository;
import stud.ntnu.no.krisefikser.repository.VerificationTokenRepository;
import org.springframework.mail.*;

import java.util.Set;
import java.util.UUID;

/**
 * Service class for handling user operations such as authentication,
 * registration, profile updates, and favorite listings management.
 */
@Service
@RequiredArgsConstructor
public class UserService {
  private static final Logger logger = LogManager.getLogger(UserService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final CustomUserDetailsService customUserDetailsService;
  private final JWTUtil jwtUtil;
  private final VerificationTokenRepository verificationTokenRepository;
  private final JavaMailSender mailSender;
  private final SecurityUtil securityUtil;
  private final CaptchaService captchaService;
  /**
   * Registers a new user with the provided details.
   *
   * @param registerRequest the registration request containing user details
   */
  public void register(RegisterRequest registerRequest) {
    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      logger.error("Email '{}' is already taken", registerRequest.getEmail());
      throw new EntityAlreadyExistsException(CustomErrorMessage.EMAIL_ALREADY_EXISTS);
    }
    logger.info("Registering user with details: {}", registerRequest);
    boolean captchaValid = captchaService.verifyToken(registerRequest.getRecaptchaToken());
    if (!captchaValid) {
      throw new AccessDeniedException("Captcha verification failed");
    }
    logger.info("Registering user with email '{}'", registerRequest.getEmail());
    User user = new User()
        .setEmail(registerRequest.getEmail())
        .setFirstName(registerRequest.getFirstName())
        .setLastName(registerRequest.getLastName())
        .setPassword(passwordEncoder.encode(registerRequest.getPassword())); //enabled is set automaticalliy to false

    UserRole role = new UserRole();
    role.setRole(Role.ROLE_USER);
    role.setUser(user);
    user.setUserRoles(Set.of(role));

    VerificationToken verificationToken = createVerificationTokenAndSendVerificationEmail(user);
    //only save user if mail is sent
    userRepository.save(user);
    verificationTokenRepository.save(verificationToken);
    logger.info("User with email '{}' registered successfully, the account is not yet enabled", registerRequest.getEmail());
  }

  /**
   * Creates a verification token for the user and sends a verification email.
   *
   * @param user the user to create a verification token for
   */
  private VerificationToken createVerificationTokenAndSendVerificationEmail(User user) {
    logger.info("Creating verification token for user '{}'", user.getEmail());
    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken = new VerificationToken().setToken(token).setUser(user);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(user.getEmail());
    message.setSubject("Complete Registration!");
    message.setText("To confirm your account, please click here: " +
        "http://localhost:5173/verify?token=" + token); //TODO make a verify page on frontend

    mailSender.send(message);
    logger.info("Verification email sent to '{}'", user.getEmail());
    return verificationToken;
  }

  /**
   * Authenticates a user and returns a JWT token as a secure cookie.
   *
   * @param request the authentication request
   * @return the response cookie containing the JWT token
   */
  public ResponseCookie authenticateAndGetCookie(LoginRequest request) {
    String token = authenticate(request);

    return securityUtil.createCookie(token);
  }

  /**
   * Authenticates a user and generates a JWT token.
   *
   * @param request the login request
   * @return the authentication response with token
   */
  public String authenticate(LoginRequest request) {
    String identity = request.getUsername() != null ? request.getUsername() : request.getEmail();

    if (identity == null || request.getPassword() == null) {
      throw new IllegalArgumentException("Missing login credentials"); //TODO: custom exception
    }

    logger.info("Authenticating user '{}'", identity);

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(identity, request.getPassword())
    );

    UserDetails userDetails = customUserDetailsService.loadUserByUsername(identity);

    String token = jwtUtil.generateToken(userDetails);

    logger.info("JWT token successfully generated for user '{}'", request.getEmail());

    return token;
  }

  /**
   * Creates a cookie to invalidate the JWT token (logout).
   *
   * @return the response cookie for logout
   */
  public ResponseCookie createLogoutCookie() {
    return securityUtil.createLogoutCookie();
  }

  /**
   * Enables a user account using the provided verification token.
   *
   * @param token the verification token
   * @throws AppEntityNotFoundException if the token is not found
   */
  public ResponseCookie enableUser(String token) {
    logger.info("Enabling user with token '{}'", token);
    VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

    if (verificationToken == null) {
      logger.error("Token '{}' not found", token);
      throw new AppEntityNotFoundException(CustomErrorMessage.TOKEN_NOT_FOUND);
    }

    User user = verificationToken.getUser();
    user.setEnabled(true);
    userRepository.save(user);
    verificationTokenRepository.delete(verificationToken);
    logger.info("User with token '{}' enabled successfully", token);

    String jwtToken = jwtUtil.generateToken(user);
    return securityUtil.createCookie(jwtToken);
  }

  public User findUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_FOUND));
  }

  public UserResponse getCurrentUser() {
    return securityUtil.getCurrentUserIfAuthenticated()
            .map(UserMapper::toDto)
            .orElse(null); // Returner null hvis ikke innlogget
  }
}
