package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for a single answer option for a quiz question.
 */
@Schema(
  description = "Represents one possible answer to a quiz question.",
  name = "QuizAnswerOptionDto"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerOptionDto {

    /**
     * The ID of this answer option.
     */
    @Schema(
      description = "Unique identifier of the answer option.",
      example     = "1001",
      required    = true
    )
    private Long id;

    /**
     * The display text of this option.
     */
    @Schema(
      description = "Text displayed to the user for this option.",
      example     = "Water",
      required    = true
    )
    private String text;
}