package stud.ntnu.no.krisefikser.dtos.reflection;

import lombok.Data;
import lombok.experimental.Accessors;
import stud.ntnu.no.krisefikser.dtos.user.UserResponse;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNoteVisibility;

import java.util.Date;

@Data
@Accessors(chain=true)
public class ReflectionNoteResponse {
  private Long id;
  private String title;
  private String content;
  private ReflectionNoteVisibility visibility;
  private UserResponse creator;
  private Date createdAt;
}
