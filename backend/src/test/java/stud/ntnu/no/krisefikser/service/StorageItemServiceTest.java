package stud.ntnu.no.krisefikser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import stud.ntnu.no.krisefikser.dtos.storageItem.StorageItemResponse;
import stud.ntnu.no.krisefikser.dtos.storageItem.StorageItemStockUpdateRequest;
import stud.ntnu.no.krisefikser.entities.Household;
import stud.ntnu.no.krisefikser.entities.ItemCategory;
import stud.ntnu.no.krisefikser.entities.ItemDefinition;
import stud.ntnu.no.krisefikser.entities.StorageItem;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.HouseholdRepository;
import stud.ntnu.no.krisefikser.repository.ItemCategoryRepository;
import stud.ntnu.no.krisefikser.repository.ItemDefinitionRepository;
import stud.ntnu.no.krisefikser.repository.StorageItemRepository;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StorageItemServiceTest {

  @Autowired
  private StorageItemService storageItemService;

  @Autowired
  private ItemCategoryRepository itemCategoryRepository;

  @Autowired
  private ItemDefinitionRepository itemRepository;

  @Autowired
  private StorageItemRepository storageItemRepository;

  @Autowired
  private HouseholdRepository householdRepository;

  private Long householdId;
  private Long categoryId;
  private Long storageItemId;

  @BeforeEach
  public void setUp() {
    storageItemRepository.deleteAll();
    itemRepository.deleteAll();
    itemCategoryRepository.deleteAll();
    householdRepository.deleteAll(); // Clear the Household repository too

    // Create a new Household entity
    Household household = new Household().setName("Test Household");

    // Save the Household first
    householdRepository.save(household);

    // Create a new ItemCategory
    ItemCategory category = new ItemCategory().setName("Emergency");

    // Save the ItemCategory
    itemCategoryRepository.save(category);

    // Create a new Item and associate it with the ItemCategory
    ItemDefinition item = new ItemDefinition()
        .setName("Flashlight")
        .setUnit("pcs")
        .setRecommendedAmountPerPerson(1.0)
        .setCategory(category);
    // Save the Item
    itemRepository.save(item);

    // Create a new StorageItem and associate it with the Household and Item
    StorageItem storageItem = new StorageItem()
        .setHousehold(household) // Link to the Household directly now
        .setItemDefinition(item)
        .setCurrentStock(5.0);

    // Save the StorageItem
    storageItemRepository.save(storageItem);

    // Set the householdId and categoryId for later use in tests
    this.householdId = household.getId();
    this.categoryId = category.getId();
    this.storageItemId = storageItem.getId();
  }

  @Test
  public void testGetStorageItemsByValidHouseholdAndCategory() {
    List<StorageItemResponse> result = storageItemService.getStorageItemsByHouseholdAndCategory(householdId,
        categoryId);

    assertNotNull(result);
    assertEquals(1, result.size());

    StorageItemResponse response = result.get(0);
    assertEquals("Flashlight", response.getName());
    assertEquals("pcs", response.getUnit());
    assertEquals(5.0, response.getCurrentStock(), 0.01);
    assertNull(response.getExpirationDate());
  }

  @Test
  public void testGetStorageItemsThrowsIfHouseholdNotFound() {
    Long invalidHouseholdId = 999L;

    AppEntityNotFoundException exception = assertThrows(AppEntityNotFoundException.class, () -> {
      storageItemService.getStorageItemsByHouseholdAndCategory(invalidHouseholdId, categoryId);
    });

    assertTrue(exception.getMessage().contains("Household not found"));
  }

  @Test
  public void testGetStorageItemsThrowsIfCategoryNotFound() {
    Long invalidCategoryId = 888L;

    AppEntityNotFoundException exception = assertThrows(AppEntityNotFoundException.class, () -> {
      storageItemService.getStorageItemsByHouseholdAndCategory(householdId, invalidCategoryId);
    });

    assertTrue(exception.getMessage().contains("Item category not found"));
  }

  // Test for updating the stock of an existing storage item
  @Test
  public void testUpdateStockSuccessfully() {
    StorageItemStockUpdateRequest updateRequest = new StorageItemStockUpdateRequest();
    updateRequest.setId(storageItemId);
    updateRequest.setNewStock(10.0);

    StorageItemResponse updatedItemResponse = storageItemService.updateStock(updateRequest);

    assertNotNull(updatedItemResponse);
    assertEquals(storageItemId, updatedItemResponse.getId());
    assertEquals(10.0, updatedItemResponse.getCurrentStock(), 0.01);
    assertNull(updatedItemResponse.getExpirationDate());
    assertEquals(10.0, updatedItemResponse.getCurrentStock(), 0.01);
  }

  // Test for updating stock where the storage item does not exist
  @Test
  public void testUpdateStockThrowsIfStorageItemNotFound() {
    StorageItemStockUpdateRequest updateRequest = new StorageItemStockUpdateRequest();
    updateRequest.setId(999L); // Non-existing ID
    updateRequest.setNewStock(10.0);

    AppEntityNotFoundException exception = assertThrows(AppEntityNotFoundException.class, () -> {
      storageItemService.updateStock(updateRequest);
    });

    assertTrue(exception.getMessage().contains("Storage item not found"));
  }

  // Test for updating stock with an increased amount
  @Test
  public void testIncreaseUpdatesRestockTimestamp() throws Exception {
    Date before = storageItemRepository.findById(storageItemId).get().getLastRestockedAt();

    Thread.sleep(10);

    // do an increase
    StorageItemStockUpdateRequest req = new StorageItemStockUpdateRequest();
    req.setId(storageItemId);
    req.setNewStock(8.0);
    storageItemService.updateStock(req);

    Date after = storageItemRepository.findById(storageItemId).get().getLastRestockedAt();
    assertTrue(after.after(before),
        () -> "Expected restock timestamp to advance on increase");
  }

  @Test
  public void testDecreaseKeepsRestockTimestamp() {
    Date before = storageItemRepository.findById(storageItemId).get().getLastRestockedAt();

    StorageItemStockUpdateRequest req = new StorageItemStockUpdateRequest();
    req.setId(storageItemId);
    req.setNewStock(3.0); // decrease from 5.0 to 3.0
    storageItemService.updateStock(req);

    Date after = storageItemRepository.findById(storageItemId).get().getLastRestockedAt();
    assertEquals(before, after,
        "Expected restock timestamp to stay the same on decrease");
  }
}