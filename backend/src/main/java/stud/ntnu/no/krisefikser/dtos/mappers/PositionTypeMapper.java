package stud.ntnu.no.krisefikser.dtos.mappers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionTypeResponse;
import stud.ntnu.no.krisefikser.dtos.map.position.PositionTypeRequest;
import stud.ntnu.no.krisefikser.entities.map.PositionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for mapping between {@link PositionType} entities and their corresponding DTOs.
 *
 * <p>
 *   Converts {@link PositionTypeRequest} objects into {@link PositionType} entities,
 *   and {@link PositionType} entities into {@link PositionTypeRequest} DTOs for API responses.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PositionTypeMapper {

  private static final Logger logger = LogManager.getLogger(PositionTypeMapper.class);

  /**
   * Converts a {@link PositionTypeRequest} DTO to a {@link PositionType} entity.
   *
   * @param request the {@link PositionTypeRequest} DTO containing the position type data
   * @return the corresponding {@link PositionType} entity
   */
  public PositionType toEntity(PositionTypeRequest request) {
    logger.debug("Mapping PositionTypeRequest to PositionType entity for position type '{}'",
        request.getName());

    return new PositionType()
        .setName(request.getName());
  }

  /**
   * Converts a {@link PositionType} entity to a {@link PositionTypeRequest} DTO.
   *
   * @param positionType the {@link PositionType} entity to convert
   * @return the corresponding {@link PositionTypeRequest} DTO
   */
  public static PositionTypeResponse toDto(PositionType positionType) {
    logger.debug("Mapping PositionType entity to PositionTypeRequest DTO for position " +
            "type ID '{}'",
        positionType.getId());

    return new PositionTypeResponse()
        .setId(positionType.getId())
        .setName(positionType.getName())
        .setDescription(positionType.getDescription());
  }
}