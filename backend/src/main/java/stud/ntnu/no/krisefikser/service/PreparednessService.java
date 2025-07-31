package stud.ntnu.no.krisefikser.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import stud.ntnu.no.krisefikser.dtos.preparedness.PreparednessSummary;
import stud.ntnu.no.krisefikser.entities.Household;
import stud.ntnu.no.krisefikser.entities.StorageItem;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.HouseholdRepository;
import stud.ntnu.no.krisefikser.repository.StorageItemRepository;
import stud.ntnu.no.krisefikser.util.DateUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Service for calculating household preparedness scores and summaries.
 */
@Service
@RequiredArgsConstructor
public class PreparednessService {

  private static final Logger logger = LogManager.getLogger(PreparednessService.class);
  private final HouseholdRepository householdRepository;
  private final StorageItemRepository storageItemRepository;
  private static final int EXPIRING_DAYS_THRESHOLD = 30; // Items expiring within 30 days


  /**
   * Calculates a detailed preparedness summary for a specific household.
   * <p>
   * This method computes various preparedness metrics based on the household's inventory:
   * <ul>
   *   <li>Overall preparedness score (0-100) calculated as the average of individual item scores</li>
   *   <li>Item adequacy based on comparison to recommended amounts per person</li>
   *   <li>Expiring items identification using the configured threshold period</li>
   * </ul>
   * </p>
   * <p>
   * The scoring algorithm works as follows:
   * <ul>
   *   <li>Items below 10% of the recommended amount score 0 points and are counted as low stock</li>
   *   <li>Items at or above 90% of the recommended amount score 100 points and are counted as adequate</li>
   *   <li>Items between 10% and 90% score proportionally on a linear scale and are counted as low stock</li>
   * </ul>
   * </p>
   *
   * @param householdId the ID of the household to analyze
   * @return a PreparednessSummary object containing calculated preparedness metrics
   * @throws AppEntityNotFoundException if the household with the given ID doesn't exist
   */
  public PreparednessSummary calculateHouseholdPreparednessSummary(Long householdId) {
    logger.info("Calculating preparedness summary for household ID: {}", householdId);

    Household household = householdRepository.findById(householdId)
        .orElseThrow(() -> {
          logger.error("Household not found with ID: {}", householdId);
          return new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND);
        });

    List<StorageItem> allStorageItems = storageItemRepository.findByHouseholdId(householdId);
    logger.info("Found {} storage items for household ID: {}", allStorageItems.size(), householdId);

    int householdSize = household.getUsers().size();
    logger.info("Household size: {}", householdSize);

    int adequateItems = 0;
    int lowStockItems = 0;
    int expiringItems = 0;
    double totalScore = 0;

    for (StorageItem item : allStorageItems) {
      double currentStock = item.getCurrentStock();
      double recommendedAmount = item.getItemDefinition().getRecommendedAmountPerPerson() * householdSize;
      double p = currentStock / recommendedAmount;

      double itemScore;
      if (p < 0.1) {
        itemScore = 0;
        lowStockItems++;
      } else if (p >= 0.9) {
        itemScore = 100;
        adequateItems++;
      } else {
        itemScore = 100 * (p - 0.1) / 0.8;
        lowStockItems++;
      }

      Date lastUpdated = item.getLastRestockedAt();
      int shelfLifeDays = item.getItemDefinition().getShelfLifeDays();

      if (isExpiringSoon(DateUtil.addDaysToDate(lastUpdated, shelfLifeDays))) {
        expiringItems++;
        itemScore *= 0.5; // Correct placement: before totalScore accumulation
      }

      totalScore += itemScore;
    }

    double overallScore = allStorageItems.isEmpty() ? 0 : (totalScore / allStorageItems.size()) / 100.0;


    PreparednessSummary summary = new PreparednessSummary();
    summary.setHouseholdId(householdId);
    summary.setHouseholdName(household.getName());
    summary.setOverallScore(overallScore);
    summary.setTotalItems(allStorageItems.size());
    summary.setAdequateItems(adequateItems);
    summary.setLowStockItems(lowStockItems);
    summary.setExpiringItems(expiringItems);

    logger.info("Preparedness summary calculated for household ID {}: Score={}, AdequateItems={}, LowStockItems={}, ExpiringItems={}",
        householdId, summary.getOverallScore(), adequateItems, lowStockItems, expiringItems);

    return summary;
  }


  /**
   * Checks if a date is within the expiring threshold (30 days from now).
   *
   * @param expirationDate the expiration date to check
   * @return true if the date is within the threshold, false otherwise
   */
  private boolean isExpiringSoon(Date expirationDate) {
    if (expirationDate == null) {
      return false;
    }

    LocalDate expiration = expirationDate.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
    LocalDate threshold = LocalDate.now().plusDays(EXPIRING_DAYS_THRESHOLD);

    return expiration.isBefore(threshold) && expiration.isAfter(LocalDate.now());
  }
}