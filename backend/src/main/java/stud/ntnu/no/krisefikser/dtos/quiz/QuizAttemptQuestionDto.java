package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO representing one question within a quiz attempt,
 * including the user’s selected answer (if any) and correctness.
 */
@Schema(
  description = "One question in the context of a quiz attempt, with the user's answer state.", 
  name        = "QuizAttemptQuestionDto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptQuestionDto {

  /**
   * The question data (id, text, and options).
   */
  @Schema(
    description = "The quiz question and its possible answer options.",
    required    = true)
  private QuizQuestionDto question;

  /**
   * The ID of the option the user selected, or null if unanswered.
   */
  @Schema(
    description = "ID of the option chosen by the user, or null if not yet answered.", 
    example     = "1001", required = false)
  private Long selectedOptionId;

  /**
   * True if the selected answer is correct; null if unanswered.
   */
  @Schema(
    description = "Whether the user's selected answer was correct; null if unanswered.", 
    example     = "true", 
    required    = false)
  private Boolean isCorrect;

  /**
   * The ID of the correct option for this question; null if unanswered.
   */
  @Schema(
    description = "ID of the correct answer option; null if the question is still unanswered.", 
    example     = "1002", 
    required    = false)
  private Long correctOptionId;
}
