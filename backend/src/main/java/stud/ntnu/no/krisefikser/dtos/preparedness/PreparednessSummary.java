package stud.ntnu.no.krisefikser.dtos.preparedness;

import lombok.*;


/**
 * Data Transfer Object (DTO) for household preparedness summary information.
 *
 * This class contains metrics that evaluate how well a household is prepared for
 * emergencies based on their inventory of essential supplies. It includes an overall
 * preparedness score and statistics about the household's storage items.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreparednessSummary {

  /**
   * The unique identifier of the household.
   */
  private Long householdId;

  /**
   * The title of the household.
   */
  private String householdName;

  /**
   * Overall preparedness score on a scale of 0-100.
   * Higher scores indicate better emergency preparedness.
   */
  private double overallScore;

  /**
   * Total number of items in the household's inventory.
   */
  private int totalItems;

  /**
   * Number of items with adequate stock levels (at least 80% of recommended amount).
   */
  private int adequateItems;

  /**
   * Number of items with insufficient stock levels (below 80% of recommended amount).
   */
  private int lowStockItems;

  /**
   * Number of items that will expire within the defined expiration threshold (usually 30 days).
   */
  private int expiringItems;

}