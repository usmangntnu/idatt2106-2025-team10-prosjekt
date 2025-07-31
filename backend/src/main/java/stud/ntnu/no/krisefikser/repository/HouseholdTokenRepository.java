package stud.ntnu.no.krisefikser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.krisefikser.entities.HouseholdToken;

public interface HouseholdTokenRepository extends JpaRepository<HouseholdToken, Long> {
  /**
   * Finds a HouseholdToken by its token.
   *
   * @param token the token to search for
   * @return the HouseholdToken with the specified token, or null if not found
   */
  HouseholdToken findByToken(String token);
}
