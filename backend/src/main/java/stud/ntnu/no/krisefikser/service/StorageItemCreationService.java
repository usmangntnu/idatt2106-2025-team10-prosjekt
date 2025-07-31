package stud.ntnu.no.krisefikser.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.krisefikser.entities.Household;
import stud.ntnu.no.krisefikser.entities.ItemDefinition;
import stud.ntnu.no.krisefikser.entities.StorageItem;
import stud.ntnu.no.krisefikser.repository.ItemDefinitionRepository;
import stud.ntnu.no.krisefikser.repository.StorageItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageItemCreationService {

  private static final Logger logger = LogManager.getLogger(StorageItemCreationService.class);
  private final StorageItemRepository storageItemRepository;
  private final ItemDefinitionRepository itemDefinitionRepository;

  @Transactional
  public void createStorageItemsForNewHousehold(Household household) {
    // Fetch all item definitions
    List<ItemDefinition> itemDefinitions = itemDefinitionRepository.findAll();

    // Iterate over all item definitions and create a StorageItem for each
    for (ItemDefinition itemDefinition : itemDefinitions) {
      // Check if a StorageItem already exists for the given household and item
      // definition
      if (storageItemRepository.existsByHouseholdIdAndItemDefinitionId(household.getId(), itemDefinition.getId())) {
        continue; // Skip if it already exists
      }

      StorageItem storageItem = new StorageItem();
      storageItem.setHousehold(household);
      storageItem.setItemDefinition(itemDefinition);
      storageItem.setCurrentStock(0.0);
      storageItemRepository.save(storageItem);
    }

    logger.info("Storage items created for new household: {}", household.getName());
  }
}
