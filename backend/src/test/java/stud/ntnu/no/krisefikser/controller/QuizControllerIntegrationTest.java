package stud.ntnu.no.krisefikser.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import stud.ntnu.no.krisefikser.entities.QuizAnswerOption;
import stud.ntnu.no.krisefikser.entities.QuizQuestion;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.repository.QuizAnswerOptionRepository;
import stud.ntnu.no.krisefikser.repository.QuizQuestionRepository;
import stud.ntnu.no.krisefikser.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class QuizControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private QuizQuestionRepository questionRepo;

  @Autowired
  private QuizAnswerOptionRepository optionRepo;

  private Long testUserId;
  private Cookie jwtCookie;

  @BeforeEach
  void setUp() throws Exception {
    // Clear any existing users
    userRepository.deleteAll();

    // Create and enable a test user
    User user = new User()
        .setFirstName("Test")
        .setLastName("User")
        .setUsername("quizuser")
        .setEmail("quiz@example.com")
        .setPassword(passwordEncoder.encode("pass123"))
        .setEnabled(true);
    userRepository.save(user);
    testUserId = user.getId();

    // Log in and capture the JWT cookie
    String loginJson = """
            { "email": "quiz@example.com", "password": "pass123" }
        """;

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginJson))
        .andExpect(status().isOk())
        .andReturn();

    Cookie cookie = loginResult.getResponse().getCookie("auth-token");
    assertNotNull(cookie, "JWT cookie should be set after login");
    this.jwtCookie = cookie;

    for (int i = 1; i <= 3; i++) {
      QuizQuestion q = new QuizQuestion()
          .setQuestionText("What is 1+1? #" + i);
      questionRepo.save(q);

      // 2) for each question, create 4 options (one correct, three incorrect)
      QuizAnswerOption correct = new QuizAnswerOption()
          .setQuizQuestion(q)
          .setText("2")
          .setCorrect(true);
      optionRepo.save(correct);
      for (int j = 0; j < 3; j++) {
        optionRepo.save(new QuizAnswerOption()
            .setQuizQuestion(q)
            .setText("Wrong " + j)
            .setCorrect(false));
      }
    }
  }

  @Test
  void testStartQuiz() throws Exception {
    String startJson = String.format("""
            { "userId": %d, "numberOfQuestions": 3 }
        """, testUserId);

    mockMvc.perform(post("/api/quizzes")
        .cookie(jwtCookie)
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(startJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.questions").isArray())
        .andExpect(jsonPath("$.questions.length()").value(3));
  }


}
