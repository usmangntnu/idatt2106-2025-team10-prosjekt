package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Entity representing one answer selected by the user during a quiz attempt.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "quiz_attempt_answers", uniqueConstraints = @UniqueConstraint(columnNames = { "attempt_id",
    "question_id" }))
public class QuizAttemptAnswer {

  /**
   * Unique identifier for the answer record.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The quiz attempt this answer belongs to.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attempt_id", nullable = false)
  private QuizAttempt attempt;

  /**
   * The question that was answered.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  private QuizQuestion question;

  /**
   * The option selected by the user.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "selected_option_id")
  private QuizAnswerOption selectedOption;
}
