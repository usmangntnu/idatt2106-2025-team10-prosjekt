package stud.ntnu.no.krisefikser.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for date-related operations.
 * <p>
 * This class provides methods to manipulate and format dates.
 * </p>
 */
public class DateUtil {

  /**
   * Utility method to add a number of days to a given date.
   *
   * @param date the original date
   * @param days the number of days to add
   * @return a new Date object with the added days
   */
  public static Date addDaysToDate(Date date, int days) {
    LocalDate localDate = date.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();

    LocalDate newLocalDate = localDate.plusDays(days);
    return Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }
}

