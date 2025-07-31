package stud.ntnu.no.krisefikser.entities;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Entity representing a quiz question.
 * <p>
 * Each question has a piece of text and can have multiple answer options.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "questions")
public class QuizQuestion {

    /**
     * Unique identifier for the question.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The text of the question.
     */
    @Column(nullable = false)
    private String questionText;

    /**
     * The answer options for the question.
     */
    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<QuizAnswerOption> answerOptions = new HashSet<>();
  
}
