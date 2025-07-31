package stud.ntnu.no.krisefikser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNote;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNoteVisibility;

import java.util.List;
import java.util.Set;

public interface ReflectionRepository extends JpaRepository<ReflectionNote, Long> {
  List<ReflectionNote> findAllByVisibilityAndUserIdNot(ReflectionNoteVisibility visibility, Long userId);
  List<ReflectionNote> findAllByUserId(Long userId);
  List<ReflectionNote> findAllByVisibilityAndUserIdIn(ReflectionNoteVisibility visibility, Set<Long> userId);
}
