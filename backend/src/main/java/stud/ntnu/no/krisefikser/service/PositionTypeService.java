package stud.ntnu.no.krisefikser.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionTypeResponse;
import stud.ntnu.no.krisefikser.dtos.mappers.PositionTypeMapper;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.PositionTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing position types.
 *
 * <p>
 * This class is responsible for handling operations related to position types,
 * such as creating, updating, retrieving, and deleting position types.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PositionTypeService {

  private static final Logger logger = LogManager.getLogger(PositionTypeService.class);
  private final PositionTypeRepository positionTypeRepository;


  /**
   * Retrieves all position types from the database.
   *
   * @return a list of all position types
   */
  public List<PositionTypeResponse> getAllPositionTypes() {
    logger.info("Fetching all position types");
    List<PositionTypeResponse> positionTypes = positionTypeRepository.findAll().stream()
        .map(PositionTypeMapper::toDto)
        .collect(Collectors.toList());


    if (positionTypes.isEmpty()) {
      logger.warn("No position types found in the database");
      throw new AppEntityNotFoundException(CustomErrorMessage.POSITION_TYPE_NOT_FOUND);
    }
    logger.info("Retrieved {} position types", positionTypes.size());
    return positionTypes;
  }
}
