package scorekeep.alex.context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Alexander Pribytok
 * Date: 17.12.2020.
 */
public enum Caller implements CallerContext {

    MOBILE_API() {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        @Override
        public String formatLocalDateToString(LocalDate date) {
            if (date == null) {
                return null;
            }
            return date.format(formatter);
        }

        @Override
        public LocalDate toLocalDateFromString(String birthday) {
            return LocalDate.parse(birthday, formatter);
        }
    };

}
