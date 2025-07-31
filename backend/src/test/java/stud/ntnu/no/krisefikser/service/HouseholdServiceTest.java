package stud.ntnu.no.krisefikser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.krisefikser.config.SecurityUtil;
import stud.ntnu.no.krisefikser.dtos.household.HouseholdRequest;
import stud.ntnu.no.krisefikser.entities.Household;
import stud.ntnu.no.krisefikser.entities.HouseholdToken;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.krisefikser.repository.HouseholdRepository;
import stud.ntnu.no.krisefikser.repository.HouseholdTokenRepository;
import stud.ntnu.no.krisefikser.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HouseholdServiceTest {
  @Mock
  SecurityUtil securityUtil;
  @Mock
  UserRepository userRepository;
  @Mock
  private HouseholdRepository householdRepository;
  @Mock
  private HouseholdTokenRepository householdTokenRepository;
  @Mock
  private StorageItemCreationService storageItemCreationService;
  
  @InjectMocks
  HouseholdService householdService;
  User user;
  @BeforeEach
  void setUp() {
    user = new User().setEmail("testuser@gmail.com").setEnabled(true);
    when(securityUtil.getCurrentUser()).thenReturn(user);
  }

  @Test
  void testCreateNewHousehold() {
    when(userRepository.save(any(User.class)))
        .thenAnswer(i -> i.getArgument(0));
    when(householdRepository.save(any(Household.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    String householdName = "Test Household";
    HouseholdRequest householdRequest = new HouseholdRequest().setName(householdName);
    householdService.createNewHousehold(householdRequest);

    ArgumentCaptor<Household> householdCaptor = ArgumentCaptor.forClass(Household.class);
    verify(householdRepository).save(householdCaptor.capture());
    Household savedHousehold = householdCaptor.getValue();
    assertEquals(householdName, savedHousehold.getName());
    assertTrue(savedHousehold.getUsers().contains(user));

    // Verify user was edited
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User updatedUser = userCaptor.getValue();
    assertEquals(savedHousehold, updatedUser.getHousehold());
  }

  @Test
  void testCreateInviteToken() {
    when(householdTokenRepository.save(any(HouseholdToken.class)))
        .thenAnswer(i -> i.getArgument(0));
    Household household = new Household();
    household.setId(123L);

    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setHousehold(household);
    when(securityUtil.getCurrentUser()).thenReturn(mockUser);

    String token = householdService.createInviteToken();
    assertNotNull(token);
    assertFalse(token.isBlank());

    ArgumentCaptor<HouseholdToken> tokenCaptor = ArgumentCaptor.forClass(HouseholdToken.class);
    verify(householdTokenRepository).save(tokenCaptor.capture());
    HouseholdToken savedToken = tokenCaptor.getValue();

    assertEquals(token, savedToken.getToken());
    assertEquals(household, savedToken.getHousehold());
  }

  @Test
  void joinHousehold_shouldAddUserToHouseholdAndDeleteToken() {
    // Arrange
    String token = " validToken ";
    Household household = new Household().setId(123L).setUsers(new ArrayList<>());

    User user = new User().setId(1L);
    HouseholdToken householdToken = new HouseholdToken().setToken(token.trim()).setHousehold(household);

    when(securityUtil.getCurrentUser()).thenReturn(user);
    when(householdTokenRepository.findByToken(token.trim())).thenReturn(householdToken);
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    // Act
    householdService.joinHousehold(token);

    // Assert
    assertTrue(household.getUsers().contains(user));
    assertEquals(household, user.getHousehold());

    verify(userRepository).save(user);
    verify(householdTokenRepository).delete(householdToken);
  }

  @Test
  void joinHousehold_shouldThrowExceptionWhenTokenNotFound() {
    // Arrange
    String token = "invalidToken";

    when(securityUtil.getCurrentUser()).thenReturn(new User());
    when(householdTokenRepository.findByToken(token)).thenReturn(null);

    // Act + Assert
    assertThrows(AppEntityNotFoundException.class, () -> householdService.joinHousehold(token));

    verify(userRepository, never()).save(any());
    verify(householdTokenRepository, never()).delete(any());
  }

  @Test
  void testCreateNewHousehold_whenUserAlreadyInHousehold_shouldThrowException() {
    user.setHousehold(new Household());
    HouseholdRequest request = new HouseholdRequest().setName("MyHouse");

    assertThrows(EntityAlreadyExistsException.class, () -> householdService.createNewHousehold(request));

    verify(householdRepository, never()).save(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void joinHousehold_shouldThrowIfUserAlreadyInHousehold() {
    User user = new User().setHousehold(new Household());
    HouseholdToken token = new HouseholdToken().setToken("token").setHousehold(new Household());

    when(securityUtil.getCurrentUser()).thenReturn(user);
    when(householdTokenRepository.findByToken("token")).thenReturn(token);

    assertThrows(EntityAlreadyExistsException.class, () -> householdService.joinHousehold("token"));

    verify(householdTokenRepository).delete(token);
    verify(userRepository, never()).save(any());
  }

  @Test
  void joinHousehold_shouldThrowIfUserAlreadyInHouseholdUsers() {
    User user = new User().setId(1L);
    Household household = new Household().setId(1L).setUsers(new ArrayList<>(List.of(user)));
    HouseholdToken token = new HouseholdToken().setToken("token").setHousehold(household);

    when(securityUtil.getCurrentUser()).thenReturn(user);
    when(householdTokenRepository.findByToken("token")).thenReturn(token);

    assertThrows(EntityAlreadyExistsException.class, () -> householdService.joinHousehold("token"));
  }

  @Test
  void leaveHousehold_shouldRemoveOwnerAndDeleteHouseholdIfLastMember() {
    Household household = new Household().setId(1L).setUsers(new ArrayList<>());
    User owner = new User().setId(1L).setEmail("owner@test.com").setHousehold(household);
    household.setUsers(new ArrayList<>(List.of(owner)));
    household.setOwner(owner);

    when(securityUtil.getCurrentUser()).thenReturn(owner);

    householdService.leaveHousehold();

    assertNull(owner.getHousehold());
    verify(userRepository).save(owner);
    verify(householdRepository).delete(household);
  }

  @Test
  void leaveHousehold_shouldRemoveOwnerAndAssignNewOwnerIfOthersRemain() {
    User owner = new User().setId(1L).setEmail("owner@test.com");
    User member = new User().setId(2L).setEmail("member@test.com");

    Household household = new Household().setId(1L)
        .setUsers(new ArrayList<>(List.of(owner, member)))
        .setOwner(owner);

    owner.setHousehold(household);
    member.setHousehold(household);

    when(securityUtil.getCurrentUser()).thenReturn(owner);

    householdService.leaveHousehold();

    assertNull(owner.getHousehold());
    assertEquals(member, household.getOwner());
    assertFalse(household.getUsers().contains(owner));
    verify(userRepository).save(owner);
    verify(householdRepository).save(household);
    verify(householdRepository, never()).delete(any());
  }

  @Test
  void leaveHousehold_shouldRemoveNonOwnerAndSaveHousehold() {
    User owner = new User().setId(1L).setEmail("owner@test.com");
    User member = new User().setId(2L).setEmail("member@test.com");

    Household household = new Household().setId(1L)
        .setUsers(new ArrayList<>(List.of(owner, member)))
        .setOwner(owner);

    owner.setHousehold(household);
    member.setHousehold(household);

    when(securityUtil.getCurrentUser()).thenReturn(member);

    householdService.leaveHousehold();

    assertNull(member.getHousehold());
    assertTrue(household.getUsers().contains(owner));
    assertFalse(household.getUsers().contains(member));
    verify(userRepository).save(member);
    verify(householdRepository).save(household);
    verify(householdRepository, never()).delete(any());
  }

  @Test
  void leaveHousehold_shouldThrowIllegalStateIfNonOwnerIsLastUser() {
    User member = new User().setId(2L).setEmail("member@test.com");
    Household household = new Household().setId(1L).setUsers(new ArrayList<>(List.of(member)));
    household.setOwner(new User().setId(999L)); // someone else

    member.setHousehold(household);

    when(securityUtil.getCurrentUser()).thenReturn(member);

    assertThrows(IllegalStateException.class, () -> householdService.leaveHousehold());

    verify(userRepository, never()).save(any());
    verify(householdRepository, never()).delete(any());
    verify(householdRepository, never()).save(any());
  }

  @Test
  void leaveHousehold_shouldThrowExceptionIfUserHasNoHousehold() {
    User user = new User().setId(3L).setEmail("nohouse@test.com");

    when(securityUtil.getCurrentUser()).thenReturn(user);

    assertThrows(AppEntityNotFoundException.class, () -> householdService.leaveHousehold());

    verify(userRepository, never()).save(any());
    verify(householdRepository, never()).delete(any());
  }




}
