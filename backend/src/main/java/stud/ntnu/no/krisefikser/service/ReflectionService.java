package stud.ntnu.no.krisefikser.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.config.SecurityUtil;
import stud.ntnu.no.krisefikser.dtos.mappers.reflections.ReflectionNoteMapper;
import stud.ntnu.no.krisefikser.dtos.reflection.ReflectionNoteRequest;
import stud.ntnu.no.krisefikser.dtos.reflection.ReflectionNoteResponse;
import stud.ntnu.no.krisefikser.entities.Household;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNote;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNoteVisibility;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.UnauthorizedOperationException;
import stud.ntnu.no.krisefikser.repository.ReflectionRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing reflection notes.
 */
@Service
@RequiredArgsConstructor
public class ReflectionService {
  private final ReflectionRepository reflectionRepository;
  private final SecurityUtil securityUtil;
  private static final Logger logger = LogManager.getLogger(ReflectionService.class);

  /**
   * Adds a reflection note for the current user.
   *
   * @param reflectionNoteRequest the request containing the reflection note details
   */
  public void addReflectionNote(ReflectionNoteRequest reflectionNoteRequest) {
    User currentUser = securityUtil.getCurrentUser();
    logger.info("Adding reflection note for user: {}", currentUser.getUsername());
    ReflectionNote reflectionNote = new ReflectionNote()
        .setUser(currentUser)
        .setContent(reflectionNoteRequest.getContent())
        .setTitle(reflectionNoteRequest.getTitle())
        .setVisibility(reflectionNoteRequest.getVisibility());
    reflectionRepository.save(reflectionNote);
    logger.info("Reflection note added successfully for user: {}", currentUser.getUsername());
  }

  /**
   * Retrieves all public reflection notes that are not created by the current user.
   *
   * @return a list of public reflection notes
   */
  public List<ReflectionNoteResponse> getAllPublicReflectionNotes() {
    User currentUser = securityUtil.getCurrentUser();
    logger.info("Fetching all public reflection notes that arent created by user: {}", currentUser.getUsername());
    List<ReflectionNote> reflectionNotes = reflectionRepository.findAllByVisibilityAndUserIdNot(ReflectionNoteVisibility.PUBLIC, currentUser.getId());
    logger.info("Fetched {} public reflection notes", reflectionNotes.size());
    return reflectionNotes.stream().map(ReflectionNoteMapper::toDto)
        .toList();
  }

  /**
   * Retrieves all reflection notes created by the current user.
   *
   * @return a list of reflection notes created by the user
   */
  public List<ReflectionNoteResponse> getAllMyReflectionNotes() {
    User currentUser = securityUtil.getCurrentUser();
    logger.info("Fetching all reflection notes created by user: {}", currentUser.getUsername());
    List<ReflectionNote> reflectionNotes = reflectionRepository.findAllByUserId(currentUser.getId());
    logger.info("Fetched {} reflection notes", reflectionNotes.size());
    return reflectionNotes.stream().map(ReflectionNoteMapper::toDto)
        .toList();
  }

  /**
   * Retrieves all reflection notes in the current user's household that are not created by the current user.
   *
   * @return a list of reflection notes in the user's household
   */
  public List<ReflectionNoteResponse> getAllReflectionNotesInMyHousehold() {
    User currentUser = securityUtil.getCurrentUser();
    Household household = currentUser.getHousehold();
    if (household == null) {
      logger.warn("User {} does not belong to any household", currentUser.getUsername());
      throw new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND);
    }
    Set<Long> userIdsInHousehold = household.getUsers().stream()
        .map(User::getId)
        .collect(Collectors.toSet());
    userIdsInHousehold.remove(currentUser.getId());
    if (userIdsInHousehold.isEmpty()) {
      logger.warn("No other users in household of user: {}", currentUser.getUsername());
      return List.of();
    }
    logger.info("Fetching all reflection notes in household of user: {}", currentUser.getUsername());
    List<ReflectionNote> reflectionNotes = reflectionRepository.findAllByVisibilityAndUserIdIn(ReflectionNoteVisibility.HOUSEHOLD, userIdsInHousehold);
    logger.info("Fetched {} reflection notes", reflectionNotes.size());
    return reflectionNotes.stream().map(ReflectionNoteMapper::toDto)
        .toList();
  }

  /**
   * Deletes a reflection note by its ID.
   *
   * @param id the ID of the reflection note
   */
  public void deleteReflectionNoteById(Long id) {
    User currentUser = securityUtil.getCurrentUser();
    logger.info("Deleting reflection note with id {} for user: {}", id, currentUser.getUsername());
    ReflectionNote reflectionNote = reflectionRepository.findById(id).orElseThrow(
        () -> new AppEntityNotFoundException(CustomErrorMessage.REFLECTION_NOTE_NOT_FOUND)
    );
    if (reflectionNote.getUser().getId().equals(currentUser.getId())) {
      reflectionRepository.delete(reflectionNote);
      logger.info("Reflection note with id {} deleted successfully for user: {}", id, currentUser.getUsername());
    } else {
      logger.warn("User {} is not authorized to delete reflection note with id {}", currentUser.getUsername(), id);
      throw new UnauthorizedOperationException(CustomErrorMessage.UNAUTHORIZED_OPERATION);
    }
  }
}
