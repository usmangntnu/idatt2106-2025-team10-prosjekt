package stud.ntnu.no.krisefikser.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    // Set up valid user data for testing
    User user = new User()
        .setFirstName("Test")
        .setLastName("User")
        .setUsername("testuser")
        .setEmail("test@example.com")
        .setPassword(passwordEncoder.encode("password123"))
        .setEnabled(true);
    userRepository.save(user);
  }

  @Test
  void testLogin() throws Exception {
    String loginRequest = "{\"email\": \"test@example.com\", \"password\": \"password123\"}";
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginRequest))
        .andExpect(status().isOk())
        .andExpect(header().string("Set-Cookie", containsString("auth-token=")));
  }
}
