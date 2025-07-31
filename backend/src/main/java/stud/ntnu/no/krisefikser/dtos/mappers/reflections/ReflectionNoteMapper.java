package stud.ntnu.no.krisefikser.dtos.mappers.reflections;

import stud.ntnu.no.krisefikser.dtos.mappers.UserMapper;
import stud.ntnu.no.krisefikser.dtos.reflection.ReflectionNoteResponse;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNote;

/**
 * Mapper class for converting ReflectionNote entities to ReflectionNoteResponse DTOs.
 */
public class ReflectionNoteMapper {
  /**
   * Converts a ReflectionNote entity to a ReflectionNoteResponse DTO.
   *
   * @param reflectionNote the ReflectionNote entity to convert
   * @return the converted ReflectionNoteResponse DTO
   */
  public static ReflectionNoteResponse toDto(ReflectionNote reflectionNote) {
    return new ReflectionNoteResponse()
        .setId(reflectionNote.getId())
        .setTitle(reflectionNote.getTitle())
        .setContent(reflectionNote.getContent())
        .setVisibility(reflectionNote.getVisibility())
        .setCreatedAt(reflectionNote.getCreatedAt())
        .setCreator(UserMapper.toDto(reflectionNote.getUser()));
  }
}
