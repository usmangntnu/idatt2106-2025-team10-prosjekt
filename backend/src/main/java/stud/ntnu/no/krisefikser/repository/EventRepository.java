package stud.ntnu.no.krisefikser.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.krisefikser.entities.map.Event;

/**
 * Repository interface for managing {@link Event} entities.
 * <p>
 * Provides CRUD operations and custom queries for events.
 * </p>
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  /**
   * Finds all events by their ID.
   *
   * @param id the ID of the event
   * @return the event with the specified ID
   */
  List<Event> findEventById(Long id);

  /**
   * Finds all events by their type ID.
   *
   * @param id the event-type ID
   * @return the event with the specified type ID
   */
  List<Event> findEventByTypeId(Long id);
}
