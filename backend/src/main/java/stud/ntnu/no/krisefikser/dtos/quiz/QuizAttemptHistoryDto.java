package stud.ntnu.no.krisefikser.dtos.quiz;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for returning the history of a quiz attempt.
 */
@Schema(
  description = "History of a quiz attempt.",
  name        = "QuizHistoryResponse"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptHistoryDto {

  @Schema(
    description = "Unique identifier for the quiz attempt.",
    example     = "123",
    required    = true
  )
  private Long attemptId;
  
  /**
   * The date when the quiz was taken.
   */
  @Schema(
    description = "Date when the quiz was taken.",
    format      = "dd-MM-yyyy",
    example     = "01:01:2023",
    required    = true
  )
  private LocalDate date;

  /**
   * The total number of questions in the quiz.
   */
  @Schema(
    description = "Total number of questions in the quiz.",
    example     = "10",
    required    = true
  )
  private int totalQuestions;

  /**
   * The number of questions answered correctly by the user.
   */
  @Schema(
    description = "Number of questions answered correctly.",
    example     = "7",
    required    = true
  )
  private int correctAnswers;

  /**
   * The status of the quiz attempt (e.g., "COMPLETED", "IN_PROGRESS").
   */
  @Schema(
    description = "Status of the quiz attempt.",
    example     = "COMPLETED",
    required    = true
  )
  private String status;
}
