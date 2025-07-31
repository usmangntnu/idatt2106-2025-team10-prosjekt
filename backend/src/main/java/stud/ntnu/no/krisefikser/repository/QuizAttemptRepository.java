package stud.ntnu.no.krisefikser.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.no.krisefikser.entities.QuizAttempt;

/**
 * Repository interface for managing QuizAttempt entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations.
 * </p>
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
  /**
   * Finds all quiz attempts for a given user ID, ordered by attempt time in
   * descending order.
   *
   * @param userId the ID of the user
   * @return a list of QuizAttempt entities
   */
  List<QuizAttempt> findByUserIdOrderByAttemptTimeDesc(Long userId);
}