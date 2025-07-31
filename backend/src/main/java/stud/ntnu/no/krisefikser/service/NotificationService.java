package stud.ntnu.no.krisefikser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.krisefikser.dtos.preparedness.NotificationRequest;
import stud.ntnu.no.krisefikser.dtos.preparedness.NotificationResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.NotificationMapper;
import stud.ntnu.no.krisefikser.entities.Household;
import stud.ntnu.no.krisefikser.entities.Notification;
import stud.ntnu.no.krisefikser.entities.NotificationType;
import stud.ntnu.no.krisefikser.entities.StorageItem;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.NotificationNotFoundException;
import stud.ntnu.no.krisefikser.repository.HouseholdRepository;
import stud.ntnu.no.krisefikser.repository.NotificationRepository;
import stud.ntnu.no.krisefikser.repository.StorageItemRepository;
import stud.ntnu.no.krisefikser.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Service for managing notifications.
 * <p>
 * This service provides methods for creating, retrieving, and deleting notifications.
 * It also automatically generates notifications for low stock and expiring items.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
  private static final Logger logger = LogManager.getLogger(NotificationService.class);

  private final NotificationRepository notificationRepository;
  private final HouseholdRepository householdRepository;
  private final StorageItemRepository storageItemRepository;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Retrieves all notifications for a specific household.
   *
   * @param householdId the ID of the household
   * @return a list of notifications for the household
   * @throws AppEntityNotFoundException if the household does not exist
   */
  public List<NotificationResponse> getNotificationsForHousehold(Long householdId) {
    logger.info("Fetching notifications for household ID: {}", householdId);

    // Validate household exists
    if (!householdRepository.existsById(householdId)) {
      logger.error("Household not found with ID: {}", householdId);
      throw new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND);
    }

    List<Notification> notifications = notificationRepository.findByHouseholdId(householdId);
    return NotificationMapper.toDtoList(notifications);
  }

  /**
   * Creates a new notification from request data.
   *
   * @param request the notification request data
   * @return the created notification response
   * @throws AppEntityNotFoundException if the household or storage item does not exist
   * @throws IllegalArgumentException if the storage item does not belong to the specified household
   */
  public NotificationResponse createNotification(NotificationRequest request) {
    logger.info("Creating notification for household ID: {}", request.getHouseholdId());

    // Find household
    Household household = householdRepository.findById(request.getHouseholdId())
        .orElseThrow(() -> {
          logger.error("Household not found with ID: {}", request.getHouseholdId());
          return new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND);
        });

    // Find storage item if provided
    StorageItem storageItem = null;
    if (request.getStorageItemId() != null) {
      storageItem = storageItemRepository.findById(request.getStorageItemId())
          .orElseThrow(() -> {
            logger.error("Storage item not found with ID: {}", request.getStorageItemId());
            return new AppEntityNotFoundException(CustomErrorMessage.STORAGE_ITEM_NOT_FOUND);
          });

      // Validate storage item belongs to household
      if (!storageItem.getHousehold().getId().equals(request.getHouseholdId())) {
        logger.error("Storage item {} does not belong to household {}",
            request.getStorageItemId(), request.getHouseholdId());
        throw new IllegalArgumentException("Storage item does not belong to the specified household");
      }
    }

    // Create notification
    Notification notification = new Notification()
        .setHousehold(household)
        .setStorageItem(storageItem)
        .setType(request.getType());

    Notification saved = notificationRepository.save(notification);
    NotificationResponse response = NotificationMapper.toDto(saved);

    // Send websocket notification
    messagingTemplate.convertAndSend("/topic/notifications", response);

    return response;
  }

  /**
   * Deletes a notification.
   *
   * @param notificationId the ID of the notification to delete
   * @throws NotificationNotFoundException if the notification does not exist
   */
  public void deleteNotification(Long notificationId) {
    logger.info("Deleting notification with ID: {}", notificationId);

    // Check if notification exists before deleting
    if (!notificationRepository.existsById(notificationId)) {
      logger.error("Notification not found with ID: {}", notificationId);
      throw new NotificationNotFoundException(CustomErrorMessage.NOTIFICATION_NOT_FOUND);
    }

    notificationRepository.deleteById(notificationId);

    // Send websocket notification for deletion
    messagingTemplate.convertAndSend("/topic/notifications/delete", notificationId);
  }

  /**
   * Checks and generates a low stock notification for an item if its stock is below threshold.
   *
   * @param item the storage item to check
   */
  @Transactional
  public void generateLowStockNotification(StorageItem item) {
    logger.info("Checking if low stock notification needed for item: {}", item.getId());

    double currentStock = item.getCurrentStock();
    double recommendedAmount = item.getItemDefinition().getRecommendedAmountPerPerson() *
        item.getHousehold().getUsers().size();

    // Define thresholds
    double lowStockThreshold = 0.4 * recommendedAmount;

    // If stock is above low threshold, remove any existing LOW_STOCK notifications
    if (currentStock >= lowStockThreshold) {
      logger.info("Stock is above low threshold ({} >= {}), removing notifications",
          currentStock, lowStockThreshold);

      // Find notifications first so we can send WebSocket messages
      List<Notification> notifications = notificationRepository
          .findByHouseholdIdAndStorageItemIdAndType(
              item.getHousehold().getId(),
              item.getId(),
              NotificationType.LOW_STOCK);

      // Delete each notification and send WebSocket message
      for (Notification notification : notifications) {
        Long notificationId = notification.getId();
        notificationRepository.deleteById(notificationId);
        messagingTemplate.convertAndSend("/topic/notifications/delete", notificationId);
      }

      return;
    }

    // Only create notification if below threshold and none exists
    List<Notification> existingNotifications = notificationRepository
        .findByHouseholdIdAndStorageItemIdAndType(
            item.getHousehold().getId(),
            item.getId(),
            NotificationType.LOW_STOCK);

    if (existingNotifications.isEmpty()) {
      logger.info("Creating low stock notification for item: {}", item.getId());
      NotificationRequest notificationRequest = new NotificationRequest();
      notificationRequest.setHouseholdId(item.getHousehold().getId());
      notificationRequest.setStorageItemId(item.getId());
      notificationRequest.setType(NotificationType.LOW_STOCK);

      createNotification(notificationRequest);
    }
  }

  @Transactional
  public void generateExpiringNotification(StorageItem item) {
    logger.info("Checking if expiration notification needed for item: {}", item.getId());

    // First check if the item has stock - if not, remove any expiration notifications
    if (item.getCurrentStock() <= 0) {
      logger.info("Item has no stock, removing expiration notifications for item: {}", item.getId());
      notificationRepository.deleteByStorageItemIdAndType(item.getId(), NotificationType.EXPIRATION);
      return;
    }

    Date lastRestocked = item.getLastRestockedAt();
    if (lastRestocked == null) {
      logger.info("No restock date for item: {}, removing notifications", item.getId());
      notificationRepository.deleteByStorageItemIdAndType(item.getId(), NotificationType.EXPIRATION);
      return;
    }

    int shelfLifeDays = item.getItemDefinition().getShelfLifeDays();
    if (shelfLifeDays <= 0) return;

    LocalDate expiresAt = DateUtil.addDaysToDate(lastRestocked, shelfLifeDays)
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
    LocalDate today = LocalDate.now();
    LocalDate cutoff = today.plusDays(7);

    logger.info("Item {}, expiresAt={}, today={}, cutoff={}", item.getId(), expiresAt, today, cutoff);

    // inclusive between today and cutoff
    if (!expiresAt.isBefore(today) && !expiresAt.isAfter(cutoff)) {
      List<Notification> existing = notificationRepository
          .findByHouseholdIdAndStorageItemIdAndType(
              item.getHousehold().getId(),
              item.getId(),
              NotificationType.EXPIRATION
          );
      if (existing.isEmpty()) {
        logger.info("Creating expiration notification for item: {}", item.getId());
        NotificationRequest req = new NotificationRequest();
        req.setHouseholdId(item.getHousehold().getId());
        req.setStorageItemId(item.getId());
        req.setType(NotificationType.EXPIRATION);
        createNotification(req);
      }
    } else {
      logger.info("Not expiring within 7 days, removing any existing notifications for item: {}", item.getId());
      notificationRepository.deleteByStorageItemIdAndType(item.getId(), NotificationType.EXPIRATION);
    }
  }

  /**
   * Checks and generates all applicable notifications for an item.
   *
   * @param item the storage item to check
   */
  @Transactional
  public void checkAndGenerateNotifications(StorageItem item) {
    generateLowStockNotification(item);
    generateExpiringNotification(item);
  }
}