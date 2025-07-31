package stud.ntnu.no.krisefikser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import stud.ntnu.no.krisefikser.dtos.itemCategory.ItemCategoryRequest;
import stud.ntnu.no.krisefikser.dtos.itemCategory.ItemCategoryResponse;
import stud.ntnu.no.krisefikser.entities.ItemCategory;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.krisefikser.repository.ItemCategoryRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ItemCategoryServiceTest {

  @Autowired
  private ItemCategoryService itemCategoryService;

  @Autowired
  private ItemCategoryRepository itemCategoryRepository;

  @BeforeEach
  public void setUp() {
    // Clean the repo before each test
    itemCategoryRepository.deleteAll();

    // Insert test data
    ItemCategory cat1 = new ItemCategory().setName("Mat");
    ItemCategory cat2 = new ItemCategory().setName("Vann");
    itemCategoryRepository.save(cat1);
    itemCategoryRepository.save(cat2);
  }

  @Test
  public void testGetAllItemCategories() {
    List<ItemCategoryResponse> categories = itemCategoryService.getAllItemCategories();

    assertNotNull(categories, "Category list should not be null");
    assertEquals(2, categories.size(), "There should be 2 categories");
    assertTrue(
      categories.stream().anyMatch(cat -> cat.getName().equals("Mat")),
      "Should contain 'Mat' category"
    );
    assertTrue(
      categories.stream().anyMatch(cat -> cat.getName().equals("Vann")),
      "Should contain 'Vann' category"
    );
  }

  @Test
  public void testCreateCategory() {
    ItemCategoryResponse newCategory = itemCategoryService.createCategory(new ItemCategoryRequest("Drikke"));

    assertNotNull(newCategory, "Created category should not be null");
    assertEquals("Drikke", newCategory.getName(), "Category title should be 'Drikke'");

    List<ItemCategoryResponse> categories = itemCategoryService.getAllItemCategories();
    assertEquals(3, categories.size(), "There should be 3 categories after creation");
    assertTrue(
      categories.stream().anyMatch(cat -> cat.getName().equals("Drikke")),
      "Should contain 'Drikke' category"
    );
  }

  @Test
  public void testCreateDuplicateCategory() {
    ItemCategoryRequest duplicateCategoryRequest = new ItemCategoryRequest("Mat");

    // Attempt to create a category that already exists
    Exception exception = assertThrows(
      EntityAlreadyExistsException.class,
      () -> itemCategoryService.createCategory(duplicateCategoryRequest)
    );

    String expectedMessage = "ItemCategory already exists.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage), "Exception message should contain 'ItemCategory already exists.'");
  }
}
