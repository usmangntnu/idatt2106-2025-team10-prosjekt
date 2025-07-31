package stud.ntnu.no.krisefikser.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.dtos.mappers.PositionMapper;
import stud.ntnu.no.krisefikser.entities.map.Position;
import stud.ntnu.no.krisefikser.entities.map.PositionType;
import stud.ntnu.no.krisefikser.exception.CustomErrorMessage;
import stud.ntnu.no.krisefikser.exception.customExceptions.AppEntityNotFoundException;
import stud.ntnu.no.krisefikser.repository.PositionRepository;
import stud.ntnu.no.krisefikser.repository.PositionTypeRepository;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionRequest;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing positions.
 *
 * <p>
 *   This class is responsible for handling position-related operations,
 *   such as creating, updating, retrieving, and deleting positions.
 *   It may also include methods for retrieving positions based on various criteria,
 *   such as proximity to a user's location or position type.
 *  </p>
 */
@Service
@RequiredArgsConstructor
public class PositionService {

  private static final Logger logger = LogManager.getLogger(EventService.class);
  private final PositionRepository positionRepository;
  private final PositionTypeRepository positionTypeRepository;
  private final PositionMapper positionMapper;

  /**
   * Adds a position to the database.
   *
   * @param request the {@link PositionRequest} containing position details
   * @return the created {@link PositionResponse} DTO
   * @throws AppEntityNotFoundException if the specified position type does not exist
   */
  @Transactional
  public PositionResponse createNewPosition(PositionRequest request) {
    logger.info("Adding new position with title: {}", request.getTitle());

    PositionType positionType = positionTypeRepository.findById(request.getTypeId())
        .orElseThrow(() ->
            new AppEntityNotFoundException(CustomErrorMessage.POSITION_TYPE_NOT_FOUND));
    logger.debug("Position type found: {}", positionType.getName());

    Position position = positionMapper.toEntity(request, positionType);
    position = positionRepository.save(position);

    logger.info("Position added with ID: {}", position.getId());
    return positionMapper.toDto(position);
  }

  /**
   * Method to update an existing position to an optional amount of fields.
   *
   * @param id the ID of the position to update
   * @param request the {@link PositionRequest} containing updated position details
   * @return the updated {@link PositionResponse} DTO
   */
  @Transactional
  public PositionResponse updatePosition(long id, PositionRequest request) {
    logger.info("Updating position: {}", id);

    Position position = positionRepository.findById(id)
        .orElseThrow(() -> new AppEntityNotFoundException(
            CustomErrorMessage.POSITION_NOT_FOUND));
    PositionType type = positionTypeRepository.findById(request.getTypeId())
        .orElseThrow(() -> new AppEntityNotFoundException(
            CustomErrorMessage.POSITION_TYPE_NOT_FOUND));

    logger.debug("Position type found: {}", type.getName());
    position.setTitle(request.getTitle());
    position.setDescription(request.getDescription());
    position.setLatitude(request.getLatitude());
    position.setLongitude(request.getLongitude());
    position.setPositionType(type);
    if (request.getCapacity() != null) {
      position.setCapacity(request.getCapacity());
    }
    position = positionRepository.save(position);
    logger.info("Position updated with ID: {}", position.getId());
    return positionMapper.toDto(position);
  }

  /**
   * Retrieves all positions from the database.
   *
   * @return a list of {@link PositionResponse} DTOs
   */
  @Transactional
  public List<PositionResponse> getAllPositions() {
    logger.info("Fetching all positions");
    List<Position> positions = positionRepository.findAll();
    logger.info("Retrieved {} positions", positions.size());
    return positions.stream()
        .map(positionMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a position by its ID.
   */
  @Transactional
  public PositionResponse getPositionById(long id) {
    logger.info("Fetching position with ID: {}", id);
    Position position = positionRepository.findById(id)
        .orElseThrow(() -> new AppEntityNotFoundException(
            CustomErrorMessage.POSITION_NOT_FOUND));
    logger.info("Retrieved position with ID: {}", position.getId());
    return positionMapper.toDto(position);
  }

  /**
   * Method to delete a position from the database by it's ID.
   *
   * @param id the ID of the position to delete
   * @throws AppEntityNotFoundException if the position does not exist
   */
  @Transactional
  public void deletePosition(long id) {
    logger.info("Deleting position with ID: {}", id);
    Position position = positionRepository.findById(id)
        .orElseThrow(() -> new AppEntityNotFoundException(
            CustomErrorMessage.POSITION_NOT_FOUND));
    positionRepository.delete(position);
    logger.info("Deleted position with ID: {}", id);
  }


  /**
   * Retrieves positions based on their position type
   */
  @Transactional
  public List<PositionResponse> getPositionsByType(long positionTypeId) {
    logger.info("Fetching positions by type with ID: {}", positionTypeId);
    List<Position> positions = positionRepository.findAllByPositionTypeId(positionTypeId);
    logger.info("Retrieved {} positions of type ID: {}", positions.size(), positionTypeId);
    return positions.stream()
        .map(positionMapper::toDto)
        .collect(Collectors.toList());
  }
}
