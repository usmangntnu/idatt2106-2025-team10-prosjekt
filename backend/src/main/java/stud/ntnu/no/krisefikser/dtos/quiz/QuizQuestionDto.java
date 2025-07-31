package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * DTO representing a quiz question and its possible answers.
 */
@Schema(
  description = "Represents one quiz question along with its possible answer options.",
  name        = "QuizQuestionDto"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDto {

    /**
     * The ID of the question.
     */
    @Schema(
      description = "Unique identifier of the quiz question.",
      example     = "101",
      required    = true
    )
    private Long questionId;

    /**
     * The text of the question.
     */
    @Schema(
      description = "The question text presented to the user.",
      example     = "What is the capital of France?",
      required    = true
    )
    private String text;

    /**
     * The list of possible answer options.
     */
    @Schema(
      description = "The list of answer options for this question.",
      required    = true
    )
    private List<QuizAnswerOptionDto> options;
}
