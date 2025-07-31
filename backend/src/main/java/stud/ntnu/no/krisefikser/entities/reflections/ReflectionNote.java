package stud.ntnu.no.krisefikser.entities.reflections;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import stud.ntnu.no.krisefikser.entities.User;

import java.util.Date;

/**
 * Entity representing a reflection note.
 * <p>
 * Reflection notes are associated with a user and can have a title and content.
 * </p>
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "reflection_notes")
public class ReflectionNote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String title;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  @Column
  private ReflectionNoteVisibility visibility;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Date createdAt;

  @JoinColumn(name = "user_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;
}
