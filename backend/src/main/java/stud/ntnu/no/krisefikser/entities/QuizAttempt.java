package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity representing an attempt by a user to take a quiz.
 * <p>
 * Records when it happened, how many questions were asked, and how many were
 * answered correctly.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "quiz_attempts")
public class QuizAttempt {

  /**
   * Unique identifier for the quiz attempt.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user who made this attempt.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * When the attempt was made.
   * <p>
   * Defaults to the current timestamp in the database.
   * </p>
   */
  @UpdateTimestamp
  @Column(name = "attempt_time", nullable = false)
  private LocalDateTime attemptTime;

  /**
   * The current status of the quiz attempt.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private QuizSessionStatus status = QuizSessionStatus.IN_PROGRESS;

  /**
   * The answers given by the user during this attempt.
   */
  @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<QuizAttemptAnswer> answers = new ArrayList<>();
}