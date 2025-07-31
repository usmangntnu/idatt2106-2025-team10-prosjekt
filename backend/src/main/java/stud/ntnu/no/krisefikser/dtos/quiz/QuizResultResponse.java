package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for returning the final result of a quiz attempt.
 */
@Schema(
  description = "Final score of a completed quiz attempt.",
  name        = "QuizResultResponse"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {

    /**
     * Total number of questions in the quiz.
     */
    @Schema(
      description = "Total questions that were part of the quiz.",
      example     = "10",
      required    = true
    )
    private Integer totalQuestions;

    /**
     * Number of questions answered correctly by the user.
     */
    @Schema(
      description = "How many questions the user answered correctly.",
      example     = "7",
      required    = true
    )
    private Integer correctAnswers;
}
