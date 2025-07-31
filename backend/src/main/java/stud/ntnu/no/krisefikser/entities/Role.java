package stud.ntnu.no.krisefikser.entities;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the different roles a user can have in the system.
 */
@Schema(description = "Defines the available user roles in the system")
public enum Role {

  @Schema(description = "Standard user with limited access")
  ROLE_USER,

  @Schema(description = "Administrator with access to system management")
  ROLE_ADMIN,

  @Schema(description = "Administrator with full access to system management")
  ROLE_SUPER_ADMIN
  }