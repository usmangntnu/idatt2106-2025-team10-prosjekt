package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * DTO for returning a full quiz payload to the client.
 */
@Schema(
  description = "Response containing the quiz attempt ID and its questions.",
  name        = "QuizResponse"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

  /**
   * Unique identifier for the quiz attempt.
   */
  @Schema(
    description = "The generated quizAttemptId for this quiz session.",
    example     = "123",
    required    = true
  )
  private Long id;

  /**
   * The list of questions included in the quiz.
   */
  @Schema(
    description = "The questions and their answer options returned for this quiz.",
    required    = true
  )
  private List<QuizQuestionDto> questions;
}
