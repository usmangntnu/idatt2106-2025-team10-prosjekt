package stud.ntnu.no.krisefikser.dtos.mappers;

import org.springframework.stereotype.Component;
import stud.ntnu.no.krisefikser.entities.QuizQuestion;
import stud.ntnu.no.krisefikser.entities.QuizAnswerOption;
import stud.ntnu.no.krisefikser.entities.QuizAttempt;
import stud.ntnu.no.krisefikser.entities.QuizAttemptAnswer;
import stud.ntnu.no.krisefikser.dtos.quiz.*;

import java.util.List;

@Component
public class QuizMapper {

  /**
   * Maps a QuizQuestion entity to a QuizQuestionDto.
   */
  public QuizQuestionDto toQuizQuestionDto(QuizQuestion question) {
    List<QuizAnswerOptionDto> quizAnswerOptionDtos = question.getAnswerOptions().stream()
        .map(this::toQuizAnswerOptionDto)
        .toList();

    return new QuizQuestionDto(
        question.getId(),
        question.getQuestionText(),
        quizAnswerOptionDtos);
  }

  /**
   * Maps a QuizAnswerOption entity to a QuizAnswerOptionDto.
   */
  public QuizAnswerOptionDto toQuizAnswerOptionDto(QuizAnswerOption quizAnswerOption) {
    return new QuizAnswerOptionDto(
        quizAnswerOption.getId(),
        quizAnswerOption.getText());
  }

  /**
   * Wraps a list of QuizQuestionDto under a QuizResponse.
   */
  public QuizResponse toQuizResponse(Long quizAttemptId,
      List<QuizQuestionDto> quizQuestionDtoList) {
    return new QuizResponse(
        quizAttemptId,
        quizQuestionDtoList);
  }

  /**
   * Maps a submitted answer into a QuizAnswerResponse.
   */
  public QuizAnswerResponse toQuizAnswerResponse(QuizAnswerRequest quizAnswerRequest,
      boolean isCorrectAnswer,
      long correctOptionId) {
    return new QuizAnswerResponse(
        quizAnswerRequest.getQuestionId(),
        quizAnswerRequest.getSelectedAnswerId(),
        isCorrectAnswer,
        correctOptionId);
  }

  /**
   * Maps a QuizAttempt entity to a QuizResultResponse.
   */
  public QuizResultResponse toQuizResultResponse(int answeredCount, int correctCount) {
    return new QuizResultResponse(
        answeredCount,
        correctCount);
  }

  public QuizAttemptHistoryDto toQuizAttemptHistoryDto(QuizAttempt quizAttempt, int answeredCount, int correctCount) {
    return new QuizAttemptHistoryDto(
        quizAttempt.getId(),
        quizAttempt.getAttemptTime().toLocalDate(),
        answeredCount,
        correctCount,
        quizAttempt.getStatus().toString());
  }

  /**
   * Maps a QuizAttempt entity to a QuizAttemptQuestionDto.
   */
  public QuizAttemptQuestionDto toQuizAttemptQuestionDto(QuizAttemptAnswer answer) {
    QuizQuestionDto questionDto = toQuizQuestionDto(answer.getQuestion());
    Long selectedOptionId = answer.getSelectedOption() != null ? answer.getSelectedOption().getId() : null;
    Boolean isCorrect = answer.getSelectedOption() != null ? answer.getSelectedOption().isCorrect() : null;
    Long correctOptionId = answer.getSelectedOption() != null
        ? answer.getQuestion().getAnswerOptions().stream()
            .filter(QuizAnswerOption::isCorrect)
            .findFirst()
            .map(QuizAnswerOption::getId)
            .orElse(null)
        : null;

    return new QuizAttemptQuestionDto(
        questionDto,
        selectedOptionId,
        isCorrect,
        correctOptionId);
  }

  /**
   * Maps a QuizAttempt entity to a QuizAttemptDto.
   */
  public QuizAttemptResponse toQuizAttemptResponse(QuizAttempt quizAttempt) {
    List<QuizAttemptQuestionDto> questionDtos = quizAttempt.getAnswers().stream()
        .map(this::toQuizAttemptQuestionDto)
        .toList();

    return new QuizAttemptResponse(
        quizAttempt.getId(),
        quizAttempt.getAttemptTime(),
        quizAttempt.getStatus().toString(),
        questionDtos);
  }
}