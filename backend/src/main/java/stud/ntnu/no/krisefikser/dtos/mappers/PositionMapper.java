package stud.ntnu.no.krisefikser.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.entities.map.Position;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionRequest;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionResponse;
import stud.ntnu.no.krisefikser.entities.map.PositionType;

/**
 * Service for mapping between {@link Position} entities and their corresponding DTOs.
 * <p>
 *  Converts {@link PositionRequest} objects into {@link Position} entities,
 *  and {@link Position} entities into {@link PositionResponse} DTOs for API responses.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PositionMapper {

  private static final Logger logger = LogManager.getLogger(PositionMapper.class);

  /**
   * Converts a {@link PositionRequest} DTO to a {@link Position} entity.
   *
   * @param request the {@link PositionRequest} DTO containing the position data
   * @return the corresponding {@link Position} entity
   */
  public Position toEntity(PositionRequest request, PositionType positionType) {
    logger.debug("Mapping PositionRequest to Position entity for position type '{}' (ID: {})",
        positionType.getName(), positionType.getId());

    Position position = new Position()
        .setTitle(request.getTitle())
        .setDescription(request.getDescription())
        .setLatitude(request.getLatitude())
        .setLongitude(request.getLongitude())
        .setPositionType(positionType);

    if (request.getCapacity() != null) {
      position.setCapacity(request.getCapacity());
    }
    logger.info("Mapped PositionRequest to Position entity: {}", position.getId());
    return position;
}

  /**
   * Converts a {@link Position} entity to a {@link PositionResponse} DTO.
   *
   * @param position the {@link Position} entity to convert
   * @return the corresponding {@link PositionResponse} DTO
   */
  public PositionResponse toDto(Position position) {
    logger.debug("Mapping Position entity to PositionResponse DTO for position ID '{}'",
        position.getId());

    PositionResponse response =  new PositionResponse()
        .setId(position.getId())
        .setTitle(position.getTitle())
        .setDescription(position.getDescription())
        .setLatitude(position.getLatitude())
        .setLongitude(position.getLongitude())
        .setCapacity(position.getCapacity())
        .setType(PositionTypeMapper.toDto(position.getPositionType()));

    if (position.getCapacity() != null) {
      response.setCapacity(position.getCapacity());
    }
    logger.info("Mapped Position entity to PositionResponse DTO: {}", position.getId());
    return response;
  }
}
