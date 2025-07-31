package stud.ntnu.no.krisefikser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import stud.ntnu.no.krisefikser.dtos.preparedness.NotificationRequest;
import stud.ntnu.no.krisefikser.dtos.preparedness.NotificationResponse;
import stud.ntnu.no.krisefikser.entities.*;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.NotificationNotFoundException;
import stud.ntnu.no.krisefikser.repository.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private HouseholdRepository householdRepository;

  @Mock
  private StorageItemRepository storageItemRepository;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @Spy @InjectMocks
  private NotificationService notificationService;


  private NotificationRequest request;
  private Household household;
  private StorageItem storageItem;
  private Notification existingNotification;

  @BeforeEach
  void setUp() {
    household = new Household();
    household.setId(1L);

    storageItem = new StorageItem();
    storageItem.setId(2L);
    storageItem.setHousehold(household);
    ItemDefinition def = new ItemDefinition();
    def.setRecommendedAmountPerPerson(5.0);
    def.setShelfLifeDays(10);
    storageItem.setItemDefinition(def);

    request = new NotificationRequest();
    request.setHouseholdId(1L);
    request.setStorageItemId(2L);
    request.setType(NotificationType.EXPIRATION);

    existingNotification = new Notification();
    existingNotification.setId(3L);
    existingNotification.setHousehold(household);
    existingNotification.setStorageItem(storageItem);
    existingNotification.setType(NotificationType.EXPIRATION);
  }

  @Test
  void getNotificationsForHousehold_valid() {
    when(householdRepository.existsById(1L)).thenReturn(true);
    when(notificationRepository.findByHouseholdId(1L))
        .thenReturn(List.of(existingNotification));

    List<NotificationResponse> list = notificationService.getNotificationsForHousehold(1L);

    assertEquals(1, list.size());
    assertEquals(existingNotification.getId(), list.get(0).getId());
    verify(notificationRepository).findByHouseholdId(1L);
  }


  @Test
  void getNotificationsForHousehold_nonExistingHousehold_throwsException() {
    // Arrange
    when(householdRepository.existsById(1L)).thenReturn(false);

    // Act & Assert
    AppEntityNotFoundException exception = assertThrows(
        AppEntityNotFoundException.class,
        () -> notificationService.getNotificationsForHousehold(1L)
    );
    assertEquals(CustomErrorMessage.HOUSEHOLD_NOT_FOUND.getMessage(), exception.getMessage());
    verify(householdRepository).existsById(1L);
    verify(notificationRepository, never()).findByHouseholdId(anyLong());
  }

  @Test
  void createNotification_withValidStorageItem_createsNotification() {
    // Arrange
    when(householdRepository.findById(1L)).thenReturn(Optional.of(household));
    when(storageItemRepository.findById(2L)).thenReturn(Optional.of(storageItem));

    // Return the existingNotification instead of the null 'notification' field
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(existingNotification);

    // Act
    NotificationResponse result = notificationService.createNotification(request);

    // Assert
    assertNotNull(result);
    assertEquals(existingNotification.getId(), result.getId());
    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());
    Notification saved = captor.getValue();
    assertEquals(household, saved.getHousehold());
    assertEquals(storageItem, saved.getStorageItem());
    assertEquals(NotificationType.EXPIRATION, saved.getType());
  }

  @Test
  void createNotification_withMissingHousehold_throwsException() {
    // Arrange
    when(householdRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    AppEntityNotFoundException exception = assertThrows(
        AppEntityNotFoundException.class,
        () -> notificationService.createNotification(request)
    );
    assertEquals(CustomErrorMessage.HOUSEHOLD_NOT_FOUND.getMessage(), exception.getMessage());
    verify(householdRepository).findById(1L);
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void createNotification_withMissingStorageItem_throwsException() {
    // Arrange
    when(householdRepository.findById(1L)).thenReturn(Optional.of(household));
    when(storageItemRepository.findById(2L)).thenReturn(Optional.empty());

    // Act & Assert
    AppEntityNotFoundException exception = assertThrows(
        AppEntityNotFoundException.class,
        () -> notificationService.createNotification(request)
    );
    assertEquals(CustomErrorMessage.STORAGE_ITEM_NOT_FOUND.getMessage(), exception.getMessage());
    verify(householdRepository).findById(1L);
    verify(storageItemRepository).findById(2L);
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void createNotification_withStorageItemFromDifferentHousehold_throwsException() {
    // Arrange
    Household differentHousehold = new Household();
    differentHousehold.setId(99L);
    storageItem.setHousehold(differentHousehold);

    when(householdRepository.findById(1L)).thenReturn(Optional.of(household));
    when(storageItemRepository.findById(2L)).thenReturn(Optional.of(storageItem));

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> notificationService.createNotification(request)
    );
    assertTrue(exception.getMessage().contains("Storage item does not belong to the specified household"));
    verify(householdRepository).findById(1L);
    verify(storageItemRepository).findById(2L);
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void createNotification_withoutStorageItem_createsNotification() {
    // Arrange
    request.setStorageItemId(null);
    when(householdRepository.findById(1L)).thenReturn(Optional.of(household));

    Notification savedNotification = new Notification();
    savedNotification.setId(3L);
    savedNotification.setHousehold(household);
    savedNotification.setStorageItem(null);
    savedNotification.setType(NotificationType.LOW_STOCK);

    when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

    // Act
    notificationService.createNotification(request);

    // Assert
    ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(notificationCaptor.capture());
    Notification capturedNotification = notificationCaptor.getValue();

    assertEquals(household, capturedNotification.getHousehold());
    assertNull(capturedNotification.getStorageItem());
  }

  @Test
  void deleteNotification_existingNotification_deletesNotification() {
    // Arrange
    Long notificationId = 3L;
    when(notificationRepository.existsById(notificationId)).thenReturn(true);

    // Act
    notificationService.deleteNotification(notificationId);

    // Assert
    verify(notificationRepository).existsById(notificationId);
    verify(notificationRepository).deleteById(notificationId);
  }

  @Test
  void deleteNotification_nonExistingNotification_throwsException() {
    // Arrange
    Long notificationId = 3L;
    when(notificationRepository.existsById(notificationId)).thenReturn(false);

    // Act & Assert
    NotificationNotFoundException exception = assertThrows(
        NotificationNotFoundException.class,
        () -> notificationService.deleteNotification(notificationId)
    );
    assertEquals(CustomErrorMessage.NOTIFICATION_NOT_FOUND.getMessage(), exception.getMessage());
    verify(notificationRepository).existsById(notificationId);
    verify(notificationRepository, never()).deleteById(anyLong());
  }

  @Test
  void generateLowStockNotification_adequateDeletes() {
    household.setUsers(List.of(new User(), new User()));
    storageItem.setCurrentStock(12.0); // recommended=10, threshold=4

    // Mock the findByHouseholdIdAndStorageItemIdAndType to return a notification
    Notification notificationToDelete = new Notification();
    notificationToDelete.setId(3L);
    when(notificationRepository.findByHouseholdIdAndStorageItemIdAndType(
        eq(1L), eq(2L), eq(NotificationType.LOW_STOCK)))
        .thenReturn(List.of(notificationToDelete));

    notificationService.generateLowStockNotification(storageItem);

    // Verify find is called as expected
    verify(notificationRepository).findByHouseholdIdAndStorageItemIdAndType(
        1L, 2L, NotificationType.LOW_STOCK);

    // Verify deleteById is called directly with the notification ID
    verify(notificationRepository).deleteById(3L);

    // Verify that a WebSocket message is sent
    verify(messagingTemplate).convertAndSend("/topic/notifications/delete", 3L);

    // Verify createNotification is never called
    verify(notificationService, never()).createNotification(any());
  }

  @Test
  void generateLowStockNotification_lowStockCreates() {
    // Setup
    household.setUsers(List.of(new User(), new User()));
    storageItem.setCurrentStock(0.2); // very below threshold

    // Mock repository calls
    when(notificationRepository.findByHouseholdIdAndStorageItemIdAndType(
        eq(1L), eq(2L), eq(NotificationType.LOW_STOCK)))
        .thenReturn(Collections.emptyList());

    // Mock householdRepository.findById() since createNotification calls it
    when(householdRepository.findById(1L)).thenReturn(Optional.of(household));

    // Mock storageItemRepository.findById() since createNotification calls it
    when(storageItemRepository.findById(2L)).thenReturn(Optional.of(storageItem));

    // Mock notification save
    Notification savedNotification = new Notification();
    savedNotification.setId(5L);
    savedNotification.setHousehold(household);
    savedNotification.setStorageItem(storageItem);
    savedNotification.setType(NotificationType.LOW_STOCK);
    when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

    // Execute
    notificationService.generateLowStockNotification(storageItem);

    // Verify repository interactions
    verify(notificationRepository).findByHouseholdIdAndStorageItemIdAndType(
        1L, 2L, NotificationType.LOW_STOCK);

    // Capture and verify the notification that was saved
    ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(notificationCaptor.capture());
    Notification capturedNotification = notificationCaptor.getValue();
    assertEquals(NotificationType.LOW_STOCK, capturedNotification.getType());
    assertEquals(household, capturedNotification.getHousehold());
    assertEquals(storageItem, capturedNotification.getStorageItem());

    // Verify WebSocket message was sent
    verify(messagingTemplate).convertAndSend(eq("/topic/notifications"), any(NotificationResponse.class));
  }

  @Test
  void generateLowStockNotification_existingNoDuplicate() {
    household.setUsers(List.of(new User(), new User()));
    storageItem.setCurrentStock(4.0);
    when(notificationRepository
        .findByHouseholdIdAndStorageItemIdAndType(1L, 2L, NotificationType.LOW_STOCK))
        .thenReturn(List.of(new Notification()));

    notificationService.generateLowStockNotification(storageItem);

    verify(notificationService, never()).createNotification(any());
  }

  @Test
  void generateExpiringNotification_noRestockDateDeletes() {
    storageItem.setLastRestockedAt(null);
    storageItem.setCurrentStock(10.0);

    notificationService.generateExpiringNotification(storageItem);

    // Verify deleteByStorageItemIdAndType is called directly
    verify(notificationRepository).deleteByStorageItemIdAndType(
        eq(2L), eq(NotificationType.EXPIRATION));

    // Fix the ambiguous method call by specifying parameter types
    verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void generateExpiringNotification_notExpiringDeletes() {
    // Set the item to expire outside the window (25 days from now)
    Date restockedDate = Date.from(Instant.now().minus(5, ChronoUnit.DAYS));
    storageItem.setLastRestockedAt(restockedDate);
    storageItem.setCurrentStock(10.0);

    // Will expire in 25 days (outside the window)
    storageItem.getItemDefinition().setShelfLifeDays(30);

    notificationService.generateExpiringNotification(storageItem);

    // Verify deleteByStorageItemIdAndType is called directly
    verify(notificationRepository).deleteByStorageItemIdAndType(
        eq(2L), eq(NotificationType.EXPIRATION));

    // Fix the ambiguous method call by specifying parameter types
    verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
  }

  @Test
  void generateExpiringNotification_withinWindowCreates() {
    storageItem.getItemDefinition().setShelfLifeDays(7);
    storageItem.setCurrentStock(10.0); // Ensure there's stock

    // last restocked 5 days ago → expires in 2 days (within 7-day window)
    storageItem.setLastRestockedAt(Date.from(Instant.now().minus(5, ChronoUnit.DAYS)));

    // Mock repository calls
    when(notificationRepository.findByHouseholdIdAndStorageItemIdAndType(
        eq(1L), eq(2L), eq(NotificationType.EXPIRATION)))
        .thenReturn(Collections.emptyList());

    // Mock householdRepository and storageItemRepository for createNotification
    when(householdRepository.findById(1L)).thenReturn(Optional.of(household));
    when(storageItemRepository.findById(2L)).thenReturn(Optional.of(storageItem));

    // Mock notification save
    Notification savedNotification = new Notification();
    savedNotification.setId(5L);
    savedNotification.setHousehold(household);
    savedNotification.setStorageItem(storageItem);
    savedNotification.setType(NotificationType.EXPIRATION);
    when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

    notificationService.generateExpiringNotification(storageItem);

    // Verify repository interactions
    verify(notificationRepository).findByHouseholdIdAndStorageItemIdAndType(
        1L, 2L, NotificationType.EXPIRATION);

    // Verify the notification is saved
    ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(notificationCaptor.capture());
    Notification capturedNotification = notificationCaptor.getValue();
    assertEquals(NotificationType.EXPIRATION, capturedNotification.getType());

    // Verify WebSocket message is sent
    verify(messagingTemplate).convertAndSend(eq("/topic/notifications"), any(NotificationResponse.class));
  }
  @Test
  void checkAndGenerateNotifications_invokesBoth() {
    doNothing().when(notificationService).generateLowStockNotification(storageItem);
    doNothing().when(notificationService).generateExpiringNotification(storageItem);

    notificationService.checkAndGenerateNotifications(storageItem);

    verify(notificationService).generateLowStockNotification(storageItem);
    verify(notificationService).generateExpiringNotification(storageItem);
  }
}
