package stud.ntnu.no.krisefikser.dtos.mappers;

import stud.ntnu.no.krisefikser.dtos.user.UserResponse;
import stud.ntnu.no.krisefikser.entities.User;


public class UserMapper {

  public static UserResponse toDto(User user) {
    return new UserResponse()
            .setId(user.getId())
            .setEmail(user.getEmail())    // Email kan være null for admin
            .setUsername(user.getUsername()) // Username kan være null for vanlig bruker
            .setFirstName(user.getFirstName())
            .setLastName(user.getLastName())
            .setRoles(user.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().name())
                    .toList())
            .setHouseholdAdmin(user.isHouseholdAdmin());
  }
}
