package stud.ntnu.no.krisefikser.dtos.reflection;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNoteVisibility;

@Data
@Accessors(chain=true)
public class ReflectionNoteRequest {
  @Schema(
    description = "Title of the reflection note",
    example     = "Sample Reflection Note",
    required    = true
  )
  @NotBlank(message = "Title cannot be blank")
  private String title;

  @Schema(
    description = "Content of the reflection note",
    example     = "This is a sample reflection note content.",
    required    = true
  )
  @NotBlank(message = "Content cannot be blank")
  private String content;

  @Schema(
    description = "Visibility of the reflection note",
    example     = "PUBLIC",
    required    = true
  )
  private ReflectionNoteVisibility visibility;
}
