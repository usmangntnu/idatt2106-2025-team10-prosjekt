package stud.ntnu.no.krisefikser.dtos.mappers;

import stud.ntnu.no.krisefikser.dtos.household.HouseholdResponse;
import stud.ntnu.no.krisefikser.entities.Household;

/**
 * Mapper class for converting Household entities to HouseholdResponse DTOs.
 */
public class HouseholdMapper {
  /**
   * Converts a Household entity to a HouseholdResponse DTO.
   *
   * @param household the Household entity to convert
   * @return the converted HouseholdResponse DTO
   */
  public static HouseholdResponse toDto(Household household) {
    //tODO need to set preparedness score correctly
    return new HouseholdResponse()
        .setId(household.getId())
        .setHouseholdName(household.getName())
        .setAddress(household.getAddress())
        .setPostalCode(household.getPostalCode())
        .setCity(household.getCity())
        .setLatitude(household.getLatitude())
        .setLongitude(household.getLongitude())
        .setUsers(household.getUsers().stream().map(UserMapper::toDto).toList())
        .setOwner(UserMapper.toDto(household.getOwner()));

  }
}
