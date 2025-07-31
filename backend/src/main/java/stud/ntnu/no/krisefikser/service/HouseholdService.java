package stud.ntnu.no.krisefikser.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.config.SecurityUtil;
import stud.ntnu.no.krisefikser.dtos.household.HouseholdRequest;
import stud.ntnu.no.krisefikser.dtos.household.HouseholdResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.HouseholdMapper;
import stud.ntnu.no.krisefikser.entities.Household;
import stud.ntnu.no.krisefikser.entities.HouseholdToken;
import stud.ntnu.no.krisefikser.entities.User;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.exception.customExceptions.EntityAlreadyExistsException;
import stud.ntnu.no.krisefikser.repository.HouseholdRepository;
import stud.ntnu.no.krisefikser.repository.HouseholdTokenRepository;
import stud.ntnu.no.krisefikser.repository.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing household-related operations.
 */
@Service
@RequiredArgsConstructor
public class HouseholdService {
  private final HouseholdRepository householdRepository;
  private final SecurityUtil securityUtil;
  private final UserRepository userRepository;
  private final StorageItemCreationService storageItemCreationService;
  private final HouseholdTokenRepository householdTokenRepository;
  private static final Logger logger = LogManager.getLogger(HouseholdService.class);

  /**
   * Creates a new household with the given title and adds the current user to it.
   *
   * @param householdRequest the request containing household details
   */
  public void createNewHousehold(HouseholdRequest householdRequest) {
    logger.info("Creating new household with title '{}'", householdRequest.getName());
    User currentUser = securityUtil.getCurrentUser();
    if (currentUser.getHousehold() != null) {
      logger.error("User '{}' already belongs to a household", currentUser.getEmail());
      throw new EntityAlreadyExistsException(CustomErrorMessage.USER_ALREADY_IN_HOUSEHOLD);
    }
    Household household = new Household()
        .setName(householdRequest.getName())
        .setUsers(List.of(currentUser))
        .setCity(householdRequest.getCity())
        .setAddress(householdRequest.getAddress())
        .setPostalCode(householdRequest.getPostalCode())
        .setLatitude(householdRequest.getLatitude())
        .setLongitude(householdRequest.getLongitude())
        .setOwner(currentUser);

    householdRepository.save(household);
    currentUser.setHousehold(household);
    userRepository.save(currentUser);
    logger.info("Adding user '{}' to household '{}'", currentUser.getEmail(), householdRequest.getName());
    storageItemCreationService.createStorageItemsForNewHousehold(household);

    logger.info("Household '{}' successfully filled with storage items", household.getName());
  }

  /**
   * Creates an invitation token that can be used once to join the current user's household.
   *
   * @return the generated invite token
   */
  public String createInviteToken() {
    User currentUser = securityUtil.getCurrentUser();

    String token = UUID.randomUUID().toString();
    HouseholdToken householdToken = new HouseholdToken()
        .setToken(token)
        .setHousehold(currentUser.getHousehold()); //The person creating the token is a part of the household
    logger.info("token with household {} created", householdToken.getHousehold());
    householdTokenRepository.save(householdToken);
    return token;
  }

  /**
   * Allows a user to join a household using the provided token.
   *
   * @param token the token used to join the household
   */
  public void joinHousehold(String token) {
    token = token.trim();
    User currentUser = securityUtil.getCurrentUser();
    HouseholdToken householdToken = householdTokenRepository.findByToken(token);
    if (householdToken == null) {
      logger.error("Invalid token '{}'", token);
      throw new AppEntityNotFoundException(CustomErrorMessage.NO_TOKEN_FOUND);
    }
    logger.info("Token found {}", householdToken);
    if (currentUser.getHousehold() != null) {
      householdTokenRepository.delete(householdToken);
      logger.error("User '{}' already belongs to a household", currentUser.getEmail());
      throw new EntityAlreadyExistsException(CustomErrorMessage.USER_ALREADY_IN_HOUSEHOLD);
    }
    if (householdToken.getHousehold().getUsers().contains(currentUser)) {
      logger.error("User '{}' is already a member of the household", currentUser.getEmail());
      throw new EntityAlreadyExistsException(CustomErrorMessage.USER_ALREADY_IN_HOUSEHOLD);
    }
    Household household = householdToken.getHousehold();
    household.getUsers().add(currentUser);
    currentUser.setHousehold(household);

    userRepository.save(currentUser);
    householdTokenRepository.delete(householdToken);
    logger.info("User '{}' joined household '{}'", currentUser.getEmail(), currentUser.getHousehold().getName());
  }

  /**
   * Checks if a household with the given ID exists.
   * 
   * @param id the ID of the household to check
   * @return true if the household exists, false otherwise
   */
  public boolean existsById(Long id) {
    logger.info("Checking if household with ID {} exists", id);
    boolean exists = householdRepository.existsById(id);
    logger.info("Household with ID {} exists: {}", id, exists);
    return exists;
  }

  /**
   * Retrieves the household of the currently authenticated user.
   *
   * @return the household of the current user
   */
  public HouseholdResponse getMyHousehold() {
    User currentUser = securityUtil.getCurrentUser();
    Household household = currentUser.getHousehold();
    if (household == null) {
      logger.error("User '{}' does not belong to any household", currentUser.getEmail());
      throw new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_IN_HOUSEHOLD);
    }
    return HouseholdMapper.toDto(household);
  }

  /**
   * Allows the current user to leave their household.
   * If the user is the last member of the household, the household is deleted.
   * If the user is the owner and there are other members, a new owner is assigned.
   * If the user is not the owner, they are simply removed from the household.
   */
  @Transactional
  public void leaveHousehold() {
    User currentUser = securityUtil.getCurrentUser();
    Household household = currentUser.getHousehold();
    if (household == null) {
      logger.error("User '{}' does not belong to any household", currentUser.getEmail());
      throw new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_IN_HOUSEHOLD);
    }
    if(currentUser.isHouseholdAdmin() && household.getUsers().size() == 1) {
      //Need to replace owner, if there are no other people the household gets deleted
      logger.info("User '{}' is the last member of the household and is the owner, also deleting household", currentUser.getEmail());
      currentUser.setHousehold(null);
      userRepository.save(currentUser);
      householdRepository.delete(household);
    } else if(currentUser.isHouseholdAdmin() && household.getUsers().size() > 1) {
      logger.info("User '{}' is the owner of the household and is leaving", currentUser.getEmail());
      currentUser.setHousehold(null);
      household.getUsers().remove(currentUser);
      userRepository.save(currentUser);
      //Set a new owner
      User newOwner = household.getUsers().get(0);//random person, the second person to join the household
      if (newOwner == null) {
        logger.error("No new owner found for household '{}'", household.getName());
        throw new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_FOUND);
      }
      household.setOwner(newOwner);
      householdRepository.save(household);
    } else if(household.getUsers().size() == 1) {
      //This shouldnt be able to happen
      logger.error("Illegal state");
      throw new IllegalStateException("User shouldnt be only user in a household and not be the owner");
    } else {
      logger.info("User '{}' is leaving the household", currentUser.getEmail());
      //user wants to leave household and isnt the admin
      currentUser.setHousehold(null);
      household.getUsers().remove(currentUser);
      userRepository.save(currentUser);
      householdRepository.save(household);
    }
  }
}
