package scorekeep.alex.context;

import java.time.LocalDate;

/**
 * @author Alexander Pribytok
 * Date: 17.12.2020.
 */
public interface CallerContext {

    String formatLocalDateToString(LocalDate date);

    LocalDate toLocalDateFromString(String birthday);
}
