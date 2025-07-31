package stud.ntnu.no.krisefikser.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stud.ntnu.no.krisefikser.dtos.mappers.QuizMapper;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAnswerRequest;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAnswerResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAttemptHistoryDto;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAttemptResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizQuestionDto;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizResultResponse;
import stud.ntnu.no.krisefikser.entities.QuizAnswerOption;
import stud.ntnu.no.krisefikser.entities.QuizAttempt;
import stud.ntnu.no.krisefikser.entities.QuizAttemptAnswer;
import stud.ntnu.no.krisefikser.entities.QuizSessionStatus;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityOperationException;
import stud.ntnu.no.krisefikser.repository.QuizAnswerOptionRepository;
import stud.ntnu.no.krisefikser.repository.QuizAttemptAnswerRepository;
import stud.ntnu.no.krisefikser.repository.QuizAttemptRepository;
import stud.ntnu.no.krisefikser.repository.QuizQuestionRepository;
import stud.ntnu.no.krisefikser.repository.UserRepository;

/**
 * Service class for managing quiz-related operations.
 * <p>
 * This class handles the logic for starting a quiz, submitting answers,
 * and retrieving results.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class QuizService {

  private static final Logger logger = LogManager.getLogger(QuizService.class);

  private final UserRepository userRepository;
  private final QuizAttemptRepository quizAttemptRepository;
  private final QuizQuestionRepository quizQuestionRepository;
  private final QuizAnswerOptionRepository quizAnswerOptionRepository;
  private final QuizAttemptAnswerRepository quizAttemptAnswerRepository;
  private final QuizMapper quizMapper;

  /**
   * Starts a new quiz attempt and returns the generated quizId plus the selected
   * questions.
   */
  @Transactional
  public QuizResponse startQuiz(Long userId, int numberOfQuestions) {
    logger.info("Starting quiz for user ID: {} with {} questions", userId, numberOfQuestions);

    if (numberOfQuestions > quizQuestionRepository.count()) {
      logger.error("Requested number of questions exceeds available questions");
      throw new AppEntityNotFoundException(CustomErrorMessage.QUIZ_QUESTION_LIMIT_EXCEEDED);
    }

    if (numberOfQuestions <= 0) {
      logger.error("Invalid number of questions: {}", numberOfQuestions);
      throw new AppEntityNotFoundException(CustomErrorMessage.QUIZ_QUESTION_LIMIT_EXCEEDED);
    }

    User quizTaker = userRepository
        .findById(userId)
        .orElseThrow(() -> {
          logger.error("User not found: {}", userId);
          return new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_FOUND);
        });

    QuizAttempt quizAttempt = new QuizAttempt()
        .setUser(quizTaker)
        .setStatus(QuizSessionStatus.IN_PROGRESS);
    quizAttemptRepository.save(quizAttempt);
    logger.info("Created QuizAttempt id={} for userId={}", quizAttempt.getId(), userId);

    List<QuizAttemptAnswer> quizAttemptAnswers = quizQuestionRepository
        .findRandomQuestions(numberOfQuestions)
        .stream()
        .map(quizQuestion -> new QuizAttemptAnswer()
            .setAttempt(quizAttempt)
            .setQuestion(quizQuestion))
        .toList();

    quizAttemptAnswerRepository.saveAll(quizAttemptAnswers);
    logger.info("Saved {} questions for QuizAttempt id={}", quizAttemptAnswers.size(), quizAttempt.getId());

    List<QuizQuestionDto> quizQuestionDtoList = quizAttemptAnswers
        .stream()
        .map(QuizAttemptAnswer::getQuestion)
        .map(quizMapper::toQuizQuestionDto)
        .toList();

    logger.info("Fetched {} questions for QuizAttempt id={}", quizQuestionDtoList.size(), quizAttempt.getId());

    return quizMapper.toQuizResponse(
        quizAttempt.getId(),
        quizQuestionDtoList);
  }

  /**
   * Processes a submitted answer, updates the attempt, and returns whether it was
   * correct.
   */
  @Transactional
  public QuizAnswerResponse submitAnswer(Long quizAttemptId, QuizAnswerRequest quizAnswerRequest) {
    logger.info("Submitting answer for quizId={} questionId={} selectedAnswerId={}",
        quizAttemptId,
        quizAnswerRequest.getQuestionId(),
        quizAnswerRequest.getSelectedAnswerId());

    // Check if selected answer option exists
    QuizAnswerOption selectedAnswerOption = quizAnswerOptionRepository
        .findById(quizAnswerRequest.getSelectedAnswerId())
        .orElseThrow(() -> {
          logger.error("AnswerOption not found: {}", quizAnswerRequest.getSelectedAnswerId());
          return new AppEntityNotFoundException(CustomErrorMessage.ANSWER_OPTION_NOT_FOUND);
        });

    // Check if the quiz attempt answer exists
    QuizAttemptAnswer quizAttemptAnswer = quizAttemptAnswerRepository
        .findByAttempt_IdAndQuestion_Id(quizAttemptId, quizAnswerRequest.getQuestionId())
        .orElseThrow(() -> {
          logger.error("QuizAttemptAnswer not found for quizAttemptId={} and questionId={}",
              quizAttemptId, quizAnswerRequest.getQuestionId());
          return new AppEntityNotFoundException(CustomErrorMessage.QUIZ_ATTEMPT_ANSWER_NOT_FOUND);
        });

    // Check if question is already answered
    if (quizAttemptAnswer.getSelectedOption() != null) {
      logger.error("Question already answered for quizAttemptId={} and questionId={}",
          quizAttemptId, quizAnswerRequest.getQuestionId());
      throw new EntityOperationException(CustomErrorMessage.QUESTION_ALREADY_ANSWERED);
    }

    // Check if the selected answer option belongs to the question
    if (!selectedAnswerOption.getQuizQuestion().getId()
        .equals(quizAnswerRequest.getQuestionId())) {
      throw new EntityOperationException(CustomErrorMessage.INVALID_ANSWER_FOR_QUESTION);
    }

    quizAttemptAnswer.setSelectedOption(selectedAnswerOption);
    quizAttemptAnswerRepository.save(quizAttemptAnswer);

    boolean isCorrectAnswer = selectedAnswerOption.isCorrect();
    Long correctAnswerId = selectedAnswerOption
        .getQuizQuestion()
        .getAnswerOptions()
        .stream()
        .filter(QuizAnswerOption::isCorrect)
        .map(QuizAnswerOption::getId)
        .findFirst()
        .orElseThrow(() -> {
          logger.error("No correct answer option found for question ID={}",
              selectedAnswerOption.getQuizQuestion().getId());
          return new EntityOperationException(CustomErrorMessage.CORRECT_ANSWER_NOT_FOUND);
        });

    completeQuizAttempt(quizAttemptId);
    return quizMapper.toQuizAnswerResponse(
        quizAnswerRequest,
        isCorrectAnswer,
        correctAnswerId);
  }

  /**
   * Retrieves the final result (total and correct answers) for a given quiz
   * attempt.
   */
  @Transactional(readOnly = true)
  public QuizResultResponse getResult(Long quizAttemptId) {
    logger.info("Fetching result for QuizAttempt id={}", quizAttemptId);

    // Check if the quiz attempt exists
    loadAttempt(quizAttemptId);

    int totalQuestions = getQuestionCount(quizAttemptId);
    int correctAnswers = getCorrectAnswersCount(quizAttemptId);
    logger.info("QuizAttempt id={} has {} total questions and {} correct answers",
        quizAttemptId, totalQuestions, correctAnswers);
    return quizMapper.toQuizResultResponse(totalQuestions, correctAnswers);
  }

  /**
   * Retrieves the history of quiz attempts for a given user.
   */
  @Transactional(readOnly = true)
  public List<QuizAttemptHistoryDto> getQuizHistory(Long userId) {
    logger.info("Fetching quiz history for user ID: {}", userId);
    List<QuizAttempt> quizAttempts = quizAttemptRepository.findByUserIdOrderByAttemptTimeDesc(userId);

    logger.info("Found {} quiz attempts for user ID: {}", quizAttempts.size(), userId);

    return quizAttempts
        .stream()
        .map(quizAttempt -> {
          int questionCount = getQuestionCount(quizAttempt.getId());
          int correctCount = getCorrectAnswersCount(quizAttempt.getId());
          return quizMapper.toQuizAttemptHistoryDto(
              quizAttempt,
              questionCount,
              correctCount);
        })
        .toList();
  }

  /**
   * Retrieves the full state of a quiz attempt, including the status and
   * per-question answers.
   * 
   * @param quizAttemptId The ID of the quiz attempt to retrieve.
   * @return A QuizAttemptResponse object containing the quiz attempt details.
   * @throws AppEntityNotFoundException if the quiz attempt is not found.
   */
  @Transactional(readOnly = true)
  public QuizAttemptResponse getQuizAttempt(Long quizAttemptId) {
    logger.info("Fetching quiz attempt details for ID: {}", quizAttemptId);
    QuizAttempt quizAttempt = loadAttempt(quizAttemptId);
    return quizMapper.toQuizAttemptResponse(quizAttempt);
  }

  /**
   * Retrieves how many questions are available in the quiz.
   */
  public Long getQuestionCount() {
    logger.info("Fetching total number of quiz questions");
    return quizQuestionRepository.count();
  }

  /**
   * Retrieves how many questions are available in the quiz for a given quiz
   * attempt.
   */
  public int getQuestionCount(Long quizAttemptId) {
    logger.info("Fetching total number of quiz questions for QuizAttempt id={}", quizAttemptId);
    return quizAttemptAnswerRepository.countByAttempt_Id(quizAttemptId);
  }

  /**
   * Retrieves how many answers has been submitted in a quiz attempt.
   */
  public int getSubmittedAnswersCount(Long quizAttemptId) {
    logger.info("Fetching submitted answer count for QuizAttempt id={}", quizAttemptId);
    return quizAttemptAnswerRepository.countByAttempt_IdAndSelectedOptionIsNotNull(quizAttemptId);
  }

  /**
   * Retrieves how many correct answers has been submitted in a quiz attempt.
   */
  public int getCorrectAnswersCount(Long quizAttemptId) {
    logger.info("Fetching correct answer count for QuizAttempt id={}", quizAttemptId);
    return quizAttemptAnswerRepository.countByAttempt_IdAndSelectedOption_IsCorrectTrue(quizAttemptId);
  }

  /**
   * Completes the quiz attempt by marking it as finished if answered all
   * questions.
   */
  public void completeQuizAttempt(Long quizAttemptId) {
    long submittedAnswersCount = getSubmittedAnswersCount(quizAttemptId);
    long totalQuestionsCount = getQuestionCount(quizAttemptId);
    logger.info("Completing quiz attempt id={} with {} submitted answers out of {} total questions",
        quizAttemptId, submittedAnswersCount, totalQuestionsCount);

    if (submittedAnswersCount == totalQuestionsCount) {
      QuizAttempt quizAttempt = loadAttempt(quizAttemptId);
      quizAttempt.setStatus(QuizSessionStatus.COMPLETED);
      quizAttemptRepository.save(quizAttempt);
      logger.info("Quiz attempt id={} marked as finished", quizAttemptId);
    }
  }

  /**
   * Load a QuizAttempt by ID or throw a 404 if it doesn't exist.
   */
  private QuizAttempt loadAttempt(Long attemptId) {
    return quizAttemptRepository.findById(attemptId)
        .orElseThrow(() -> {
          logger.error("QuizAttempt not found: {}", attemptId);
          return new AppEntityNotFoundException(CustomErrorMessage.QUIZ_ATTEMPT_NOT_FOUND);
        });
  }

}
