package stud.ntnu.no.krisefikser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stud.ntnu.no.krisefikser.dtos.preparedness.PreparednessSummary;
import stud.ntnu.no.krisefikser.entities.*;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreparednessServiceTest {

  private HouseholdRepository householdRepository;
  private StorageItemRepository storageItemRepository;
  private PreparednessService preparednessService;

  @BeforeEach
  void setup() {
    householdRepository = mock(HouseholdRepository.class);
    storageItemRepository = mock(StorageItemRepository.class);
    preparednessService = new PreparednessService(householdRepository, storageItemRepository);
  }

  @Test
  void testCalculatePreparednessSummary_validInput_success() {
    // Given
    Long householdId = 1L;

    Household household = new Household();
    household.setId(householdId);
    household.setName("Test Household");

    User user1 = new User();
    User user2 = new User();
    household.setUsers(List.of(user1, user2)); // household size = 2

    when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));

    ItemDefinition cannedBeans = new ItemDefinition();
    cannedBeans.setRecommendedAmountPerPerson(5.0);
    cannedBeans.setShelfLifeDays(10);

    StorageItem item = new StorageItem();
    item.setItemDefinition(cannedBeans);
    item.setCurrentStock(8.9);
    item.setHousehold(household);
    item.setLastRestockedAt(new Date()); // Set to current date

    when(storageItemRepository.findByHouseholdId(householdId))
        .thenReturn(List.of(item));

    // When
    PreparednessSummary result = preparednessService.calculateHouseholdPreparednessSummary(householdId);

    // Then
    assertEquals(householdId, result.getHouseholdId());
    assertEquals("Test Household", result.getHouseholdName());
    assertEquals(1, result.getTotalItems());
    assertEquals(0, result.getAdequateItems());
    assertEquals(1, result.getLowStockItems());
    assertEquals(1, result.getExpiringItems());
    assertTrue(result.getOverallScore() < 100); // since score isn't full
  }

  @Test
  void testCalculatePreparednessSummary_householdNotFound_throwsException() {
    // Given
    Long householdId = 999L;
    when(householdRepository.findById(householdId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(AppEntityNotFoundException.class,
        () -> preparednessService.calculateHouseholdPreparednessSummary(householdId));
  }

  @Test
  void testCalculatePreparednessSummary_allItemsAdequate_success() {
    Long householdId = 2L;

    Household household = new Household();
    household.setId(householdId);
    household.setName("Adequate Household");
    household.setUsers(List.of(new User(), new User())); // household size = 2

    when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));

    ItemDefinition itemDefinition = new ItemDefinition();
    itemDefinition.setRecommendedAmountPerPerson(5.0);
    itemDefinition.setShelfLifeDays(100); // Long shelf life

    StorageItem storageItem = new StorageItem();
    storageItem.setItemDefinition(itemDefinition);
    storageItem.setCurrentStock(10.0); // Exactly 100% of recommended
    storageItem.setHousehold(household);
    storageItem.setLastRestockedAt(new Date()); // Set to current date

    when(storageItemRepository.findByHouseholdId(householdId))
        .thenReturn(List.of(storageItem));

    PreparednessSummary result = preparednessService.calculateHouseholdPreparednessSummary(householdId);

    assertEquals(1, result.getAdequateItems());
    assertEquals(0, result.getLowStockItems());
    assertEquals(0, result.getExpiringItems());
    assertEquals(1.0, result.getOverallScore());
  }

  @Test
  void testCalculatePreparednessSummary_expiringItem_detected() {
    Long householdId = 3L;

    Household household = new Household();
    household.setId(householdId);
    household.setName("Expiring Test");
    household.setUsers(List.of(new User())); // size = 1

    when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));

    ItemDefinition itemDefinition = new ItemDefinition();
    itemDefinition.setRecommendedAmountPerPerson(1.0);
    itemDefinition.setShelfLifeDays(10); // Will expire in 10 days

    StorageItem storageItem = new StorageItem();
    storageItem.setItemDefinition(itemDefinition);
    storageItem.setCurrentStock(1.0);
    storageItem.setHousehold(household);
    storageItem.setLastRestockedAt(new Date()); // Current date, will expire in 10 days

    when(storageItemRepository.findByHouseholdId(householdId))
        .thenReturn(List.of(storageItem));

    PreparednessSummary result = preparednessService.calculateHouseholdPreparednessSummary(householdId);

    assertEquals(1, result.getExpiringItems());

    // Expected score:
    // Recommended = 1.0 per person, actual = 1.0
    // Ratio = 1.0, so raw score = 100
    // Expiring → apply 0.5 penalty → final score = 50.0
    assertEquals(0.5, result.getOverallScore());
  }

  @Test
  void testCalculatePreparednessSummary_noItems_returnsZeroScore() {
    Long householdId = 4L;

    Household household = new Household();
    household.setId(householdId);
    household.setName("Empty Household");
    household.setUsers(List.of(new User())); // household size = 1

    when(householdRepository.findById(householdId)).thenReturn(Optional.of(household));
    when(storageItemRepository.findByHouseholdId(householdId))
        .thenReturn(List.of()); // no items

    PreparednessSummary result = preparednessService.calculateHouseholdPreparednessSummary(householdId);

    assertEquals(0, result.getTotalItems());
    assertEquals(0, result.getOverallScore());
  }
}