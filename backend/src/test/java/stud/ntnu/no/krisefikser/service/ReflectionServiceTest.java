package stud.ntnu.no.krisefikser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.krisefikser.config.SecurityUtil;
import stud.ntnu.no.krisefikser.dtos.reflection.ReflectionNoteRequest;
import stud.ntnu.no.krisefikser.dtos.reflection.ReflectionNoteResponse;
import stud.ntnu.no.krisefikser.entities.Household;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNote;
import stud.ntnu.no.krisefikser.entities.reflections.ReflectionNoteVisibility;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.UnauthorizedOperationException;
import stud.ntnu.no.krisefikser.repository.ReflectionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReflectionServiceTest {

  @Mock
  private ReflectionRepository reflectionRepository;

  @Mock
  private SecurityUtil securityUtil;

  @InjectMocks
  private ReflectionService reflectionService;

  private User currentUser;
  private User householdMember;
  private Household household;
  private ReflectionNote reflectionNote;
  private ReflectionNoteRequest reflectionNoteRequest;

  @BeforeEach
  void setUp() {
    // Setup current user
    currentUser = new User();
    currentUser.setId(1L);
    currentUser.setUsername("currentUser");

    // Setup household member
    householdMember = new User();
    householdMember.setId(2L);
    householdMember.setUsername("householdMember");

    // Setup household
    household = new Household();
    household.setId(1L);
    household.setName("Test Household");
    List<User> users = Arrays.asList(currentUser, householdMember);
    household.setUsers(users);

    // Set household for users
    currentUser.setHousehold(household);
    householdMember.setHousehold(household);

    // Setup reflection note
    reflectionNote = new ReflectionNote()
        .setId(1L)
        .setTitle("Test Note")
        .setContent("Test Content")
        .setVisibility(ReflectionNoteVisibility.PUBLIC)
        .setUser(currentUser);

    // Setup reflection note request
    reflectionNoteRequest = new ReflectionNoteRequest();
    reflectionNoteRequest.setTitle("Test Title");
    reflectionNoteRequest.setContent("Test Content");
    reflectionNoteRequest.setVisibility(ReflectionNoteVisibility.PUBLIC);
  }

  @Test
  @DisplayName("Should add reflection note successfully")
  void addReflectionNote_ShouldSaveReflectionNote() {
    // Arrange
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);

    // Act
    reflectionService.addReflectionNote(reflectionNoteRequest);

    // Assert
    verify(reflectionRepository, times(1)).save(any(ReflectionNote.class));
  }

  @Test
  @DisplayName("Should get all public reflection notes except current user's")
  void getAllPublicReflectionNotes_ShouldReturnPublicNotesExceptCurrentUsers() {
    // Arrange
    ReflectionNote note1 = new ReflectionNote()
        .setId(2L)
        .setTitle("Public Note 1")
        .setContent("Content 1")
        .setVisibility(ReflectionNoteVisibility.PUBLIC)
        .setUser(householdMember);

    ReflectionNote note2 = new ReflectionNote()
        .setId(3L)
        .setTitle("Public Note 2")
        .setContent("Content 2")
        .setVisibility(ReflectionNoteVisibility.PUBLIC)
        .setUser(householdMember);

    List<ReflectionNote> notes = Arrays.asList(note1, note2);

    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(reflectionRepository.findAllByVisibilityAndUserIdNot(ReflectionNoteVisibility.PUBLIC, currentUser.getId()))
        .thenReturn(notes);

    // Act
    List<ReflectionNoteResponse> result = reflectionService.getAllPublicReflectionNotes();

    // Assert
    assertEquals(2, result.size());
    verify(reflectionRepository, times(1))
        .findAllByVisibilityAndUserIdNot(ReflectionNoteVisibility.PUBLIC, currentUser.getId());
  }

  @Test
  @DisplayName("Should get all reflection notes of current user")
  void getAllMyReflectionNotes_ShouldReturnUserNotes() {
    // Arrange
    ReflectionNote note1 = new ReflectionNote()
        .setId(1L)
        .setTitle("My Note 1")
        .setContent("Content 1")
        .setVisibility(ReflectionNoteVisibility.PRIVATE)
        .setUser(currentUser);

    ReflectionNote note2 = new ReflectionNote()
        .setId(2L)
        .setTitle("My Note 2")
        .setContent("Content 2")
        .setVisibility(ReflectionNoteVisibility.PUBLIC)
        .setUser(currentUser);

    List<ReflectionNote> notes = Arrays.asList(note1, note2);

    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(reflectionRepository.findAllByUserId(currentUser.getId())).thenReturn(notes);

    // Act
    List<ReflectionNoteResponse> result = reflectionService.getAllMyReflectionNotes();

    // Assert
    assertEquals(2, result.size());
    verify(reflectionRepository, times(1)).findAllByUserId(currentUser.getId());
  }

  @Test
  @DisplayName("Should get all household reflection notes except current user's")
  void getAllReflectionNotesInMyHousehold_ShouldReturnHouseholdNotes() {
    // Arrange
    ReflectionNote note1 = new ReflectionNote()
        .setId(2L)
        .setTitle("Household Note 1")
        .setContent("Content 1")
        .setVisibility(ReflectionNoteVisibility.HOUSEHOLD)
        .setUser(householdMember);

    List<ReflectionNote> notes = List.of(note1);

    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(reflectionRepository.findAllByVisibilityAndUserIdIn(
        eq(ReflectionNoteVisibility.HOUSEHOLD), anySet()))
        .thenReturn(notes);

    // Act
    List<ReflectionNoteResponse> result = reflectionService.getAllReflectionNotesInMyHousehold();

    // Assert
    assertEquals(1, result.size());
    verify(reflectionRepository, times(1)).findAllByVisibilityAndUserIdIn(
        eq(ReflectionNoteVisibility.HOUSEHOLD), anySet());
  }

  @Test
  @DisplayName("Should throw exception when user has no household")
  void getAllReflectionNotesInMyHousehold_WithNoHousehold_ShouldThrowException() {
    // Arrange
    currentUser.setHousehold(null);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);

    // Act & Assert
    AppEntityNotFoundException exception = assertThrows(
        AppEntityNotFoundException.class,
        () -> reflectionService.getAllReflectionNotesInMyHousehold()
    );
    assertEquals(CustomErrorMessage.HOUSEHOLD_NOT_FOUND, exception.getErrorMessage());
  }

  @Test
  @DisplayName("Should return empty list when no other users in household")
  void getAllReflectionNotesInMyHousehold_WithNoOtherUsers_ShouldReturnEmptyList() {
    // Arrange
    // Create a new household with only the current user
    Household singleUserHousehold = new Household();
    singleUserHousehold.setId(2L);
    singleUserHousehold.setName("Single User Household");
    List<User> singleUserSet = List.of(currentUser);
    singleUserHousehold.setUsers(singleUserSet);
    currentUser.setHousehold(singleUserHousehold);

    when(securityUtil.getCurrentUser()).thenReturn(currentUser);

    // Act
    List<ReflectionNoteResponse> result = reflectionService.getAllReflectionNotesInMyHousehold();

    // Assert
    assertTrue(result.isEmpty());
    verify(reflectionRepository, never()).findAllByVisibilityAndUserIdIn(any(), anySet());
  }

  @Test
  @DisplayName("Should delete reflection note successfully")
  void deleteReflectionNoteById_WithValidIdAndOwner_ShouldDeleteNote() {
    // Arrange
    Long noteId = 1L;
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(reflectionRepository.findById(noteId)).thenReturn(Optional.of(reflectionNote));

    // Act
    reflectionService.deleteReflectionNoteById(noteId);

    // Assert
    verify(reflectionRepository, times(1)).delete(reflectionNote);
  }

  @Test
  @DisplayName("Should throw exception when reflection note not found")
  void deleteReflectionNoteById_WithInvalidId_ShouldThrowException() {
    // Arrange
    Long invalidNoteId = 999L;
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(reflectionRepository.findById(invalidNoteId)).thenReturn(Optional.empty());

    // Act & Assert
    AppEntityNotFoundException exception = assertThrows(
        AppEntityNotFoundException.class,
        () -> reflectionService.deleteReflectionNoteById(invalidNoteId)
    );
    assertEquals(CustomErrorMessage.REFLECTION_NOTE_NOT_FOUND, exception.getErrorMessage());
  }

  @Test
  @DisplayName("Should throw exception when user tries to delete another user's note")
  void deleteReflectionNoteById_WithNonOwner_ShouldThrowException() {
    // Arrange
    Long noteId = 1L;
    // Create a reflection note owned by someone else
    ReflectionNote otherUserNote = new ReflectionNote()
        .setId(noteId)
        .setTitle("Other User's Note")
        .setContent("Content")
        .setVisibility(ReflectionNoteVisibility.PUBLIC)
        .setUser(householdMember);

    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(reflectionRepository.findById(noteId)).thenReturn(Optional.of(otherUserNote));

    // Act & Assert
    UnauthorizedOperationException exception = assertThrows(
        UnauthorizedOperationException.class,
        () -> reflectionService.deleteReflectionNoteById(noteId)
    );
    assertEquals(CustomErrorMessage.UNAUTHORIZED_OPERATION, exception.getErrorMessage());
    verify(reflectionRepository, never()).delete(any());
  }
}