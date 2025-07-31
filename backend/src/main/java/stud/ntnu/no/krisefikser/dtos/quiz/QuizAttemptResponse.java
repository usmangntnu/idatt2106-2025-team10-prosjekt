package stud.ntnu.no.krisefikser.dtos.quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Full details of one quiz attempt:
 * metadata plus per-question state.
 */
@Schema(description = "Full details of a quiz attempt, including status and per-question answers.", name = "QuizAttemptResponse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponse {
  @Schema(description = "ID of the quiz attempt.", example = "42", required = true)
  private Long attemptId;

  @Schema(description = "Timestamp when the quiz attempt began.", example = "2025-05-06T14:30:00", required = true)
  private LocalDateTime attemptTime;

  @Schema(description = "Current status of the quiz attempt.", example = "IN_PROGRESS", required = true)
  private String status;

  @Schema(description = "List of questions and the user's answer state for each.", required = true)
  private List<QuizAttemptQuestionDto> questions;
}
