package stud.ntnu.no.krisefikser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.no.krisefikser.entities.QuizAnswerOption;

/**
 * Repository interface for managing QuizAnswerOption entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations.
 * </p>
 */
@Repository
public interface QuizAnswerOptionRepository extends JpaRepository<QuizAnswerOption, Long> {

} 
