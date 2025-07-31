package stud.ntnu.no.krisefikser.dtos.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stud.ntnu.no.krisefikser.dtos.map.event.CircleDataDto;
import stud.ntnu.no.krisefikser.entities.map.CircleData;
import stud.ntnu.no.krisefikser.entities.map.Event;


/**
 * Service for mapping between {@link CircleDataDto} and {@link Event} entities.
 * <p>
 * This class is responsible for converting circle data from DTOs to entities and vice versa.
 * It handles the conversion of circle data, which includes center coordinates and radius.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CircleDataMapper {

  private static final Logger logger = LogManager.getLogger(CircleDataMapper.class);

  /**
   * Converts a {@link CircleDataDto} to a {@link CircleData} entity.
   *
   * @param dto the CircleDataDto to convert
   * @return the converted CircleData entity
   */
  public static CircleData toEntity(CircleDataDto dto) {

    logger.debug("Mapping CircleDataDto to CircleData entity");

    CircleData circleData = new CircleData();
    circleData.setRadius(dto.getRadius());
    circleData.setLongitude(dto.getLongitude());
    circleData.setLatitude(dto.getLatitude());

    logger.info("CircleData entity created with center: ({}, {}) and radius: {}",
        circleData.getLongitude(), circleData.getLatitude(), circleData.getRadius());
    return circleData;
}

  /**
   * Converts a {@link CircleData} entity to a {@link CircleDataDto}.
   *
   * @param entity the CircleData entity to convert
   * @return the converted CircleDataDto
   */
  public static CircleDataDto toDto(CircleData entity) {

    logger.debug("Mapping CircleData entity to CircleDataDto");

    CircleDataDto dto = new CircleDataDto();
    dto.setRadius(entity.getRadius());
    dto.setLongitude(entity.getLongitude());
    dto.setLatitude(entity.getLatitude());
    logger.info("CircleDataDto created with center: ({}, {}) and radius: {}",
        dto.getLongitude(), dto.getLatitude(), dto.getRadius());
    return dto;
  }
}
