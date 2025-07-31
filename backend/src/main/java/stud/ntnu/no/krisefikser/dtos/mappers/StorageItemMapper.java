package stud.ntnu.no.krisefikser.dtos.mappers;

import java.util.Date;

import org.springframework.stereotype.Component;
import stud.ntnu.no.krisefikser.dtos.storageItem.StorageItemResponse;
import stud.ntnu.no.krisefikser.entities.StorageItem;
import stud.ntnu.no.krisefikser.entities.ItemDefinition;
import stud.ntnu.no.krisefikser.util.DateUtil;

/**
 * Mapper class for converting {@link StorageItem} entities to
 * {@link StorageItemResponse} DTOs.
 * <p>
 * This class provides a method to convert a {@link StorageItem} entity to a
 * {@link StorageItemResponse} DTO.
 * </p>
 */
@Component
public class StorageItemMapper {

  /**
   * Converts a StorageItem entity to a StorageItemResponse DTO.
   *
   * @param storageItem the entity to be converted
   * @return the corresponding StorageItemResponse DTO
   */
  public static StorageItemResponse toDto(StorageItem storageItem) {
    if (storageItem == null) {
      return null;
    }

    StorageItemResponse response = new StorageItemResponse();

    ItemDefinition item = storageItem.getItemDefinition();
    if (item != null) {
      response.setId(storageItem.getId());
      double currentStock = storageItem.getCurrentStock();
      response.setCurrentStock(currentStock);
      response.setCategoryId(item.getCategory().getId());
      response.setName(item.getName());
      response.setUnit(item.getUnit());
      double recommendedStock = item.getRecommendedAmountPerPerson();
      response.setRecommendedStockForHousehold(recommendedStock * storageItem.getHousehold().getUsers().size());
      Date lastUpdated = storageItem.getLastRestockedAt();
      int shelfLifeDays = item.getShelfLifeDays();
      if (shelfLifeDays == 0 || currentStock == 0) {
        response.setExpirationDate(null);
      } else {
        response.setExpirationDate(DateUtil.addDaysToDate(lastUpdated, shelfLifeDays));

      }
      double stockCompletionPercentage = response.getCurrentStock() / response.getRecommendedStockForHousehold();
      response.setStockCompletionPercentage(stockCompletionPercentage);
    }
    return response;
  }
}
