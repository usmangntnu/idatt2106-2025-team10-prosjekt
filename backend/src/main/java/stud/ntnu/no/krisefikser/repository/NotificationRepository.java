package stud.ntnu.no.krisefikser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stud.ntnu.no.krisefikser.entities.Notification;
import stud.ntnu.no.krisefikser.entities.NotificationType;

import java.util.List;

/**
 * Repository interface for managing Notification entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations.
 * </p>
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  /**
   * Finds all notifications for a specific household.
   *
   * @param householdId the ID of the household
   * @return a list of notifications for the household
   */
  List<Notification> findByHouseholdId(Long householdId);

  List<Notification> findByHouseholdIdAndStorageItemIdAndType(Long householdId, Long storageItemId, NotificationType type);
  void deleteByStorageItemIdAndType(Long storageItemId, NotificationType type);

}
