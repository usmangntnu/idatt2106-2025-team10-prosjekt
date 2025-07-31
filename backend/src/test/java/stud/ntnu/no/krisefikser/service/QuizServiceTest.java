package stud.ntnu.no.krisefikser.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import stud.ntnu.no.krisefikser.dtos.quiz.QuizAnswerRequest;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAnswerResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAttemptHistoryDto;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAttemptQuestionDto;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAttemptResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizQuestionDto;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizResultResponse;
import stud.ntnu.no.krisefikser.entities.QuizAnswerOption;
import stud.ntnu.no.krisefikser.entities.QuizAttempt;
import stud.ntnu.no.krisefikser.entities.QuizQuestion;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityOperationException;
import stud.ntnu.no.krisefikser.repository.QuizAnswerOptionRepository;
import stud.ntnu.no.krisefikser.repository.QuizAttemptRepository;
import stud.ntnu.no.krisefikser.repository.QuizQuestionRepository;
import stud.ntnu.no.krisefikser.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class QuizServiceTest {

  @Autowired
  private QuizService quizService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private QuizQuestionRepository quizQuestionRepository;

  @Autowired
  private QuizAnswerOptionRepository quizAnswerOptionRepository;

  @Autowired
  private QuizAttemptRepository quizAttemptRepository;

  private User testUser;
  private QuizQuestion testQuizQuestion;
  private QuizAnswerOption correctOption;
  private QuizAnswerOption incorrectOption;

  @BeforeEach
  public void setUp() {
    // Clean repositories
    quizAttemptRepository.deleteAll();
    quizAnswerOptionRepository.deleteAll();
    quizQuestionRepository.deleteAll();
    userRepository.deleteAll();

    // Create a test user
    testUser = new User()
        .setFirstName("Test")
        .setLastName("User")
        .setEmail("test.user@example.com")
        .setUsername("testuser")
        .setPassword("password")
        .setEnabled(true);
    userRepository.save(testUser);

    // Create a test question
    testQuizQuestion = new QuizQuestion();
    testQuizQuestion.setQuestionText("What is 2 + 2?");
    quizQuestionRepository.save(testQuizQuestion);

    // Create one correct and one incorrect answer option
    correctOption = new QuizAnswerOption();
    correctOption.setText("4");
    correctOption.setCorrect(true);
    correctOption.setQuizQuestion(testQuizQuestion);
    quizAnswerOptionRepository.save(correctOption);

    incorrectOption = new QuizAnswerOption();
    incorrectOption.setText("3");
    incorrectOption.setCorrect(false);
    incorrectOption.setQuizQuestion(testQuizQuestion);
    quizAnswerOptionRepository.save(incorrectOption);
  }

  @Test
  public void testStartQuizSuccess() {
    QuizResponse response = quizService.startQuiz(testUser.getId(), 1);

    assertNotNull(response, "QuizResponse should not be null");
    assertNotNull(response.getId(), "Quiz ID should not be null");

    List<QuizQuestionDto> questions = response.getQuestions();
    assertNotNull(questions, "Questions list should not be null");
    assertEquals(1, questions.size(), "Should return 1 question");

    QuizQuestionDto questionDto = questions.get(0);
    assertEquals(testQuizQuestion.getId(), questionDto.getQuestionId(), "Question ID should match");
    assertEquals(testQuizQuestion.getQuestionText(), questionDto.getText(), "Question text should match");
    assertEquals(2, questionDto.getOptions().size(), "Should have 2 options");
    assertTrue(
        questionDto.getOptions().stream().anyMatch(opt -> opt.getId().equals(correctOption.getId())),
        "Options should include the correct option");
    assertTrue(
        questionDto.getOptions().stream().anyMatch(opt -> opt.getId().equals(incorrectOption.getId())),
        "Options should include the incorrect option");

    Optional<QuizAttempt> persistedAttempt = quizAttemptRepository.findById(response.getId());
    assertTrue(persistedAttempt.isPresent(), "QuizAttempt should be saved");
    QuizAttempt attempt = persistedAttempt.get();

    assertEquals(1, quizService.getQuestionCount(attempt.getId()), "Question count should be 1");
    assertEquals(0, quizService.getSubmittedAnswersCount(attempt.getId()), "Submitted answers count should be 0");
    assertEquals(0, quizService.getCorrectAnswersCount(attempt.getId()), "Correct answers count should be 0");

  }

  @Test
  public void testSubmitAnswerCorrect() {
    QuizResponse startResponse = quizService.startQuiz(testUser.getId(), 1);

    QuizAnswerRequest answerRequest = new QuizAnswerRequest();
    answerRequest.setUserId(testUser.getId());
    answerRequest.setQuestionId(testQuizQuestion.getId());
    answerRequest.setSelectedAnswerId(correctOption.getId());

    QuizAnswerResponse answerResponse = quizService.submitAnswer(startResponse.getId(), answerRequest);

    assertTrue(answerResponse.isCorrectAnswer(), "Answer should be marked correct");
    assertEquals(correctOption.getId(), answerResponse.getCorrectAnswerId(), "Correct answer ID should match");

    QuizResultResponse result = quizService.getResult(startResponse.getId());
    assertEquals(1, result.getCorrectAnswers(), "Correct answers should be 1");
    assertEquals(1, result.getTotalQuestions(), "Total questions should be 1");
  }

  @Test
  public void testSubmitAnswerIncorrect() {
    QuizResponse startResponse = quizService.startQuiz(testUser.getId(), 1);

    QuizAnswerRequest answerRequest = new QuizAnswerRequest();
    answerRequest.setUserId(testUser.getId());
    answerRequest.setQuestionId(testQuizQuestion.getId());
    answerRequest.setSelectedAnswerId(incorrectOption.getId());

    QuizAnswerResponse answerResponse = quizService.submitAnswer(startResponse.getId(), answerRequest);

    assertFalse(answerResponse.isCorrectAnswer(), "Answer should be marked incorrect");
    assertEquals(correctOption.getId(), answerResponse.getCorrectAnswerId(), "Correct answer ID should still match");

    QuizResultResponse result = quizService.getResult(startResponse.getId());
    assertEquals(0, result.getCorrectAnswers(), "Correct answers should be 0");
    assertEquals(1, result.getTotalQuestions(), "Total questions should be 1");
  }

  @Test
  public void testSubmitAnswerWithNonexistentOption() {
    QuizAnswerRequest answerRequest = new QuizAnswerRequest();
    answerRequest.setUserId(testUser.getId());
    answerRequest.setQuestionId(testQuizQuestion.getId());
    answerRequest.setSelectedAnswerId(999L);

    assertThrows(
        AppEntityNotFoundException.class,
        () -> quizService.submitAnswer(1L, answerRequest),
        "Should throw if answer option does not exist");
  }

  @Test
  public void testSubmitAnswerWithNonexistentQuizAttempt() {
    QuizAnswerRequest answerRequest = new QuizAnswerRequest();
    answerRequest.setUserId(testUser.getId());
    answerRequest.setQuestionId(testQuizQuestion.getId());
    answerRequest.setSelectedAnswerId(correctOption.getId());

    assertThrows(
        AppEntityNotFoundException.class,
        () -> quizService.submitAnswer(999L, answerRequest),
        "Should throw if quiz attempt does not exist");
  }

  @Test
  public void testGetResultSuccess() {
    QuizResponse startResponse = quizService.startQuiz(testUser.getId(), 1);
    QuizResultResponse resultResponse = quizService.getResult(startResponse.getId());

    assertEquals(0, resultResponse.getCorrectAnswers(), "Initial correctAnswers should be 0");
    assertEquals(1, resultResponse.getTotalQuestions(), "TotalQuestions should be 1");
  }

  @Test
  public void testGetResultWithNonexistentQuizAttempt() {
    assertThrows(
        AppEntityNotFoundException.class,
        () -> quizService.getResult(999L),
        "Should throw if quiz attempt is not found");
  }

  @Test
  public void testGetQuizHistoryWhenNoAttempts() {
    List<QuizAttemptHistoryDto> history = quizService.getQuizHistory(testUser.getId());
    assertNotNull(history, "History list should not be null");
    assertTrue(history.isEmpty(), "History should be empty when no attempts exist");
  }

  @Test
  public void testGetQuizHistoryAfterAttempts() {
    // First attempt: answer correctly
    QuizResponse firstResponse = quizService.startQuiz(testUser.getId(), 1);
    QuizAnswerRequest firstAnswer = new QuizAnswerRequest();
    firstAnswer.setUserId(testUser.getId());
    firstAnswer.setQuestionId(testQuizQuestion.getId());
    firstAnswer.setSelectedAnswerId(correctOption.getId());
    quizService.submitAnswer(firstResponse.getId(), firstAnswer);

    // Second attempt: answer incorrectly
    QuizResponse secondResponse = quizService.startQuiz(testUser.getId(), 1);
    QuizAnswerRequest secondAnswer = new QuizAnswerRequest();
    secondAnswer.setUserId(testUser.getId());
    secondAnswer.setQuestionId(testQuizQuestion.getId());
    secondAnswer.setSelectedAnswerId(incorrectOption.getId());
    quizService.submitAnswer(secondResponse.getId(), secondAnswer);

    List<QuizAttemptHistoryDto> history = quizService.getQuizHistory(testUser.getId());
    assertEquals(2, history.size(), "History should contain two entries");

    // Verify entries contain correct data
    boolean sawCorrectFirst = history.stream().anyMatch(h -> h.getTotalQuestions() == 1 &&
        h.getCorrectAnswers() == 1 &&
        h.getDate().equals(LocalDate.now()));

    assertTrue(sawCorrectFirst, "Should include entry with 1 correct answer");

    boolean sawCorrectSecond = history.stream().anyMatch(h -> h.getTotalQuestions() == 1 &&
        h.getCorrectAnswers() == 0 &&
        h.getDate().equals(LocalDate.now()));

    assertTrue(sawCorrectSecond, "Should include entry with 0 correct answers");
  }

  @Test
  public void testStartQuizWithZeroQuestionsThrows() {
    assertThrows(
        AppEntityNotFoundException.class,
        () -> quizService.startQuiz(testUser.getId(), 0),
        "Should reject zero questions");
  }

  @Test
  public void testStartQuizWithTooManyQuestionsThrows() {
      long availableQuestions = quizQuestionRepository.count();
      assertThrows(
          AppEntityNotFoundException.class,
          () -> quizService.startQuiz(testUser.getId(), (int) availableQuestions + 1),
          "Should reject more than available questions"
      );
  }
  
  @Test
  public void testSubmitAnswerTwiceThrows() {
      QuizResponse firstStartResponse = quizService.startQuiz(testUser.getId(), 1);
      QuizAnswerRequest firstAnswerRequest = new QuizAnswerRequest(
          testUser.getId(),
          testQuizQuestion.getId(),
          correctOption.getId()
      );
  
      // First submit goes through
      quizService.submitAnswer(firstStartResponse.getId(), firstAnswerRequest);
  
      // Second submit on same question should fail
      EntityOperationException exception = assertThrows(
          EntityOperationException.class,
          () -> quizService.submitAnswer(firstStartResponse.getId(), firstAnswerRequest),
          "Should not allow answering the same question twice"
      );
      assertTrue(exception.getMessage().contains("already answered"));
  }
  
  @Test
  public void testGetQuizAttemptDetailsBeforeAndAfterAnswer() {
      QuizResponse startResponse = quizService.startQuiz(testUser.getId(), 1);
      Long attemptId = startResponse.getId();
  
      // Before answering
      QuizAttemptResponse detailsBeforeAnswer = quizService.getQuizAttempt(attemptId);
      QuizAttemptQuestionDto questionDtoBefore = detailsBeforeAnswer.getQuestions().get(0);
      assertNull(questionDtoBefore.getSelectedOptionId(), "No selected option before answering");
      assertNull(questionDtoBefore.getIsCorrect(), "Correctness should be null before answering");
      assertNull(questionDtoBefore.getCorrectOptionId(), "CorrectOptionId should be null before answering");
  
      // Submit an answer
      QuizAnswerRequest answerRequest = new QuizAnswerRequest(
          testUser.getId(),
          testQuizQuestion.getId(),
          correctOption.getId()
      );
      quizService.submitAnswer(attemptId, answerRequest);
  
      // After answering
      QuizAttemptResponse detailsAfterAnswer = quizService.getQuizAttempt(attemptId);
      QuizAttemptQuestionDto questionDtoAfter = detailsAfterAnswer.getQuestions().get(0);
      assertEquals(correctOption.getId(), questionDtoAfter.getSelectedOptionId(), "SelectedOptionId should match after answering");
      assertTrue(questionDtoAfter.getIsCorrect(), "isCorrect should be true for the correct answer");
      assertEquals(correctOption.getId(), questionDtoAfter.getCorrectOptionId(), "correctOptionId should match the correct answer");
  }
  
  @Test
  public void testGetQuizAttemptThrowsIfNotFound() {
      assertThrows(
          AppEntityNotFoundException.class,
          () -> quizService.getQuizAttempt(999L),
          "Should throw when attempt not found"
      );
  }
}