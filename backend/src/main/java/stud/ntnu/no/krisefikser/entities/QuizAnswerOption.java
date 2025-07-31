package stud.ntnu.no.krisefikser.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Entity representing one possible answer to a question.
 * <p>
 * Holds the text of the option and whether it is the correct one.
 * </p>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "answer_options")
public class QuizAnswerOption {

    /**
     * Unique identifier for the answer option.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The text of the answer option.
     */
    @Column(nullable = false)
    private String text;

    /**
     * Indicates if this answer option is correct.
     */
    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    /**
     * The question this answer option belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion quizQuestion; 
}
