package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for submitting quiz answers.
 */
@Schema(
  description = "Payload for submitting one answer in a quiz attempt.",
  name = "QuizAnswerRequest"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerRequest {

  /**
   * The ID of the user who answered the quiz question.
   */
  @Schema(
    description = "ID of the user submitting the answer.",
    example     = "42",
    required    = true
  )
  @NotBlank(message = "userId cannot be blank")
  private Long userId;

  /**
   * The ID of the question being answered.
   */
  @Schema(
    description = "ID of the quiz question being answered.",
    example     = "101",
    required    = true
  )
  @NotBlank(message = "questionId cannot be blank")
  private Long questionId;

  /**
   * The ID of the answer selected by the user.
   */
  @Schema(
    description = "ID of the selected answer option.",
    example     = "1003",
    required    = true
  )
  @NotBlank(message = "selectedAnswerId cannot be blank")
  private Long selectedAnswerId;
}
