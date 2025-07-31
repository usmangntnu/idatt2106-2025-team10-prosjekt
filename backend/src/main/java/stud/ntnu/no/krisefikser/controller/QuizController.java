package stud.ntnu.no.krisefikser.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import stud.ntnu.no.krisefikser.dtos.quiz.QuizAnswerRequest;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAnswerResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAttemptHistoryDto;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizAttemptResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizRequest;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizResponse;
import stud.ntnu.no.krisefikser.dtos.quiz.QuizResultResponse;
import stud.ntnu.no.krisefikser.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Endpoints for starting quizzes, submitting answers, and retrieving results")
public class QuizController {

  private static final Logger logger = LogManager.getLogger(QuizController.class);

  private final QuizService quizService;

  /**
   * Starts a new quiz attempt for a user.
   *
   * @param quizRequest contains userId and numberOfQuestions
   * @return the quizId and the list of questions
   */
  @Operation(summary = "Start a new quiz", description = "Creates a new quiz attempt for the specified user with the requested number of questions.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Quiz started successfully"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "400", description = "Invalid request (e.g., too many questions)")
  })
  @PostMapping
  public ResponseEntity<QuizResponse> startQuiz(
      @Valid @RequestBody QuizRequest quizRequest) {
    logger.info("Received startQuiz request: userId={}, numberOfQuestions={}",
        quizRequest.getUserId(), quizRequest.getNumberOfQuestions());

    QuizResponse quizResponse = quizService.startQuiz(
        quizRequest.getUserId(),
        quizRequest.getNumberOfQuestions());

    logger.info("Started quiz attempt: quizId={} for userId={}",
        quizResponse.getId(), quizRequest.getUserId());

    return ResponseEntity.ok(quizResponse);
  }

  /**
   * Submits an answer for a given quiz question.
   *
   * @param quizId            the ID of the quiz attempt
   * @param quizAnswerRequest contains questionId, selectedAnswerId, and userId
   * @return whether the submitted answer was correct
   */
  @Operation(summary = "Submit an answer", description = "Submits one answer for the specified quiz attempt and returns correctness.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Answer processed successfully"),
      @ApiResponse(responseCode = "404", description = "Quiz attempt or answer option not found"),
      @ApiResponse(responseCode = "500", description = "Internal processing error")
  })
  @PostMapping("/{quizId}/answers")
  public ResponseEntity<QuizAnswerResponse> submitAnswer(
      @PathVariable Long quizId,
      @Valid @RequestBody QuizAnswerRequest quizAnswerRequest) {
    logger.info("Received submitAnswer: quizId={}, questionId={}, selectedAnswerId={}",
        quizId,
        quizAnswerRequest.getQuestionId(),
        quizAnswerRequest.getSelectedAnswerId());

    QuizAnswerResponse answerResponse = quizService.submitAnswer(quizId, quizAnswerRequest);

    logger.info("Processed answer for quizId={} questionId={}: correct={}",
        quizId,
        answerResponse.getQuestionId(),
        answerResponse.isCorrectAnswer());

    return ResponseEntity.ok(answerResponse);
  }

  /**
   * Retrieves the final result (score) of a quiz attempt.
   *
   * @param quizId the ID of the quiz attempt
   * @return total questions and correct answers count
   */
  @Operation(summary = "Get quiz result", description = "Fetches the final score for the specified quiz attempt.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Result retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Quiz attempt not found")
  })
  @GetMapping("/{quizId}/result")
  public ResponseEntity<QuizResultResponse> getResult(@PathVariable Long quizId) {
    logger.info("Received getResult request for quizId={}", quizId);

    QuizResultResponse resultResponse = quizService.getResult(quizId);

    logger.info("Returning result for quizId={}: {}/{} correct",
        quizId,
        resultResponse.getCorrectAnswers(),
        resultResponse.getTotalQuestions());

    return ResponseEntity.ok(resultResponse);
  }

  /**
   * Retrieves the quiz attempt history for a user.
   *
   * @param userId the ID of the user
   * @return list of quiz attempts with their results
   */
  @Operation(summary = "Get quiz history", description = "Fetches the quiz attempt history for the specified user.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Quiz history retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping("/history/{userId}")
  public ResponseEntity<List<QuizAttemptHistoryDto>> getQuizHistory(@PathVariable Long userId) {
    logger.info("Received getQuizHistory request for userId={}", userId);

    List<QuizAttemptHistoryDto> quizHistory = quizService.getQuizHistory(userId);

    logger.info("Returning quiz history for userId={} with {} attempts",
        userId, quizHistory.size());

    return ResponseEntity.ok(quizHistory);
  }

  /**
   * Retrieves the full state of a quiz attempt, including per-question answers.
   *
   * @param quizId the ID of the quiz attempt
   * @return metadata plus the list of questions with selectedOptionId, isCorrect,
   *         and correctOptionId
   */
  @Operation(summary = "Get quiz attempt details", description = "Fetches the full state of the specified quiz attempt, including which questions have been answered, the user's selected answer, and the correct answer.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Quiz attempt details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Quiz attempt not found")
  })
  @GetMapping("/{quizId}")
  public ResponseEntity<QuizAttemptResponse> getQuizAttempt(
      @PathVariable("quizId") Long quizId) {
    logger.info("Received getQuizAttempt request for quizId={}", quizId);

    QuizAttemptResponse response = quizService.getQuizAttempt(quizId);

    logger.info("Returning details for quizId={}, {} questions",
        quizId, response.getQuestions().size());
    return ResponseEntity.ok(response);
  }
}
