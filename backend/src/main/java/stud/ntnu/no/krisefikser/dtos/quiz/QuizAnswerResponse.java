package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for returning quiz answer details.
 */
@Schema(
  description = "Response after submitting an answer, indicating correctness.",
  name = "QuizAnswerResponse"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerResponse {

  /**
   * The ID of the question associated with this answer.
   */
  @Schema(
    description = "ID of the question that was answered.",
    example     = "101",
    required    = true
  )
  private Long questionId;

  /**
   * The ID of the selected answer.
   */
  @Schema(
    description = "ID of the answer option that the user selected.",
    example     = "1003",
    required    = true
  )
  private Long selectedAnswerId;

  /**
   * Boolean indicating whether the selected answer is correct.
   */
  @Schema(
    description = "True if the submitted answer is correct, false otherwise.",
    example     = "true",
    required    = true
  )
  private boolean correctAnswer;

  /**
   * The ID of the correct answer.
   */
  @Schema(
    description = "ID of the correct answer option for this question.",
    example     = "1003",
    required    = true
  )
  private Long correctAnswerId;
}
