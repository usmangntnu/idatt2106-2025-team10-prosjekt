package stud.ntnu.no.krisefikser.entities.map;

/**
 * Enum representing the status of an event.
 * <p>
 * The status of an event can be one of the following:
 * <ul>
 *    <li>UPCOMING: The event is scheduled to occur in the future.</li>
 *    <li>ACTIVE: The event is currently happening.</li>
 *    <li>INACTIVE: The event is not currently happening, but may occur again in the future.</li>
 *    <li>FINISHED: The event has concluded.</li>
 *   </ul>
 * </p>
 */
public enum EventStatus {
  /**
   * Represents an event that is scheduled to occur in the future.
   */
  UPCOMING,
  /**
   * Represents an event that is currently happening.
   */
  ACTIVE,
  /**
   * Represents an event that is not currently happening, but may occur again in the future.
   */
  INACTIVE,
  /**
   * Represents an event that has concluded.
   */
  FINISHED
}
