package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO for requesting a new quiz.
 */
@Schema(
  description = "Payload to request a new quiz with a given number of questions.",
  name        = "QuizRequest"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {

    /**
     * The ID of the user requesting the quiz.
     */
    @Schema(
      description = "Identifier of the user who wants to take the quiz.",
      example     = "42",
      required    = true
    )
    @NotBlank(message = "userId cannot be blank")
    private Long userId;

    /**
     * The number of questions to include in the quiz.
     */
    @Schema(
      description = "How many questions the quiz should contain.",
      example     = "10",
      required    = true
    )
    @NotBlank(message = "numberOfQuestions cannot be blank")
    @PositiveOrZero(message = "numberOfQuestions cannot be negative")
    private Integer numberOfQuestions;
}
