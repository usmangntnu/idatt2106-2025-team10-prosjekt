package stud.ntnu.no.krisefikser.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.no.krisefikser.entities.QuizAttemptAnswer;

/**
 * Repository interface for managing QuizAttemptAnswer entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations.
 * </p>
 */
@Repository
public interface QuizAttemptAnswerRepository extends JpaRepository<QuizAttemptAnswer, Long> {
  /**
   * Count how many submitted answers in this attempt point to a correct option.
   */
  int countByAttempt_IdAndSelectedOption_IsCorrectTrue(Long attemptId);

  /**
   * Count how many answers could be submitted in this attempt.
   */
  int countByAttempt_Id(Long attemptId);

  /**
   * Count how many answers were submitted in this attempt.
   */
  int countByAttempt_IdAndSelectedOptionIsNotNull(Long attemptId);

  /**
   * Find the answer by attempt and question id.
   */
  Optional<QuizAttemptAnswer> findByAttempt_IdAndQuestion_Id(Long attemptId, Long questionId);
  



}