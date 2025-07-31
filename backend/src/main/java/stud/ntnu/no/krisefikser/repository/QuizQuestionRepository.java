package stud.ntnu.no.krisefikser.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import stud.ntnu.no.krisefikser.entities.QuizQuestion;

/**
 * Repository interface for managing QuizQuestion entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations.
 * It also includes a custom method to fetch a random set of quiz questions.
 * </p>
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
  /**
   * Fetches a random set of quiz questions based on the specified limit.
   */
  @NativeQuery(value = "SELECT * FROM questions ORDER BY RAND() LIMIT :limit")
  List<QuizQuestion> findRandomQuestions(@Param("limit") int limit);
}