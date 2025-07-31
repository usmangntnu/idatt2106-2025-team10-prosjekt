package stud.ntnu.no.krisefikser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import stud.ntnu.no.krisefikser.config.JWTUtil;
import stud.ntnu.no.krisefikser.config.SecurityUtil;
import stud.ntnu.no.krisefikser.dtos.auth.LoginRequest;
import stud.ntnu.no.krisefikser.dtos.auth.RegisterRequest;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.entities.VerificationToken;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.krisefikser.repository.UserRepository;
import stud.ntnu.no.krisefikser.repository.VerificationTokenRepository;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private CaptchaService captchaService;

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private CustomUserDetailsService customUserDetailsService;
  @Mock private JWTUtil jwtUtil;
  @Mock
  private JavaMailSender mailSender;
  @Mock
  private SecurityUtil securityUtil;
  @Mock private VerificationTokenRepository verificationTokenRepository;

  @InjectMocks
  private UserService userService;

  private User user;
  private RegisterRequest validRequest;

  @BeforeEach
  void setUp() {
    user = new User()
        .setEmail("test@gmail.com")
        .setFirstName("Test")
        .setLastName("User")
        .setPassword("encodedPassword")
        .setEnabled(true);

    validRequest = new RegisterRequest()
        .setEmail("newuser@example.com")
        .setFirstName("New")
        .setLastName("User")
        .setPassword("plainPassword");
  }

  @Test
  void testAuthenticate_success() {
    LoginRequest loginRequest = new LoginRequest().setEmail(user.getEmail()).setPassword("password123");

    when(customUserDetailsService.loadUserByUsername(user.getEmail())).thenReturn(user);
    when(jwtUtil.generateToken(user)).thenReturn("mockToken");

    String token = userService.authenticate(loginRequest);

    assertNotNull(token);
    verify(authenticationManager).authenticate(any());
    verify(jwtUtil).generateToken(any());
  }

  @Test
  void testAuthenticate_disabledUser() {
    user.setEnabled(false);
    LoginRequest loginRequest = new LoginRequest().setEmail(user.getEmail()).setPassword("password123");

    doThrow(new DisabledException("User is disabled"))
        .when(authenticationManager)
        .authenticate(any());

    assertThrows(DisabledException.class, () -> userService.authenticate(loginRequest));
  }

  @Test
  void testAuthenticate_userNotFound() {
    LoginRequest invalidRequest = new LoginRequest().setEmail("notfound@example.com").setPassword("pass");

    doThrow(new AuthenticationException("Bad credentials") {})
        .when(authenticationManager)
        .authenticate(any());

    assertThrows(AuthenticationException.class, () -> userService.authenticate(invalidRequest));
  }

  @Test
  void register_shouldRegisterUserAndSendVerificationEmail() {
    when(captchaService.verifyToken(validRequest.getRecaptchaToken())).thenReturn(true);
    when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
    when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    when(verificationTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    doNothing().when(mailSender).send(any(SimpleMailMessage.class));

    assertDoesNotThrow(() -> userService.register(validRequest));

    verify(userRepository).existsByEmail(validRequest.getEmail());
    verify(userRepository).save(any(User.class));
    verify(verificationTokenRepository).save(any(VerificationToken.class));
    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  void register_shouldThrowExceptionWhenEmailExists() {
    when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

    assertThrows(EntityAlreadyExistsException.class, () -> userService.register(validRequest));

    verify(userRepository, never()).save(any());
    verify(verificationTokenRepository, never()).save(any());
    verify(mailSender, never()).send(any(SimpleMailMessage.class));
  }

  @Test
  void register_shouldStillSaveUserIfEmailSendingFails() {
    when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
    when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
    when(captchaService.verifyToken(validRequest.getRecaptchaToken())).thenReturn(true);


    doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));

    assertThrows(RuntimeException.class, () -> userService.register(validRequest));

    verify(userRepository, never()).save(any(User.class));
    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  void register_shouldThrowExceptionWhenCaptchaFails() {
    when(captchaService.verifyToken(validRequest.getRecaptchaToken())).thenReturn(false);
    assertThrows(AccessDeniedException.class, () -> userService.register(validRequest));
  }

  @Test
  void testAuthenticate_invalidCredentials() {
    LoginRequest invalidRequest = new LoginRequest().setEmail("wrongemail@example.com").setPassword("wrongpassword");

    doThrow(new AuthenticationException("Bad credentials") {})
        .when(authenticationManager)
        .authenticate(any());

    assertThrows(AuthenticationException.class, () -> userService.authenticate(invalidRequest));
  }

  @Test
  void enableUser_shouldThrowExceptionWhenTokenNotFound() {
    String token = "nonexistentToken";
    when(verificationTokenRepository.findByToken(token)).thenReturn(null);

    assertThrows(AppEntityNotFoundException.class, () -> userService.enableUser(token));
  }

  @Test
  void enableUser_shouldEnableUserSuccessfully() {
    String token = "validToken";
    User user = new User().setEmail("test@gmail.com");
    VerificationToken verificationToken = new VerificationToken().setToken(token).setUser(user);
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    when(jwtUtil.generateToken(user)).thenReturn("mockToken");
    when(securityUtil.createCookie("mockToken")).thenReturn(ResponseCookie.from("auth-token", "token").build());
    when(userRepository.save(any(User.class))).thenReturn(user);

    userService.enableUser(token);

    verify(userRepository).save(user);
    verify(verificationTokenRepository).delete(verificationToken);
  }

  @Test
  void createLogoutCookie_shouldCreateCookie() {
    when(securityUtil.createLogoutCookie()).thenCallRealMethod();
    ResponseCookie cookie = userService.createLogoutCookie();

    assertNotNull(cookie);
    assertEquals("auth-token", cookie.getName());
    assertTrue(cookie.getMaxAge().compareTo(Duration.ZERO) == 0);
  }

  @Test
  void findUserByEmail_shouldThrowExceptionWhenUserNotFound() {
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    assertThrows(AppEntityNotFoundException.class, () -> userService.findUserByEmail("nonexistent@example.com"));
  }



}

