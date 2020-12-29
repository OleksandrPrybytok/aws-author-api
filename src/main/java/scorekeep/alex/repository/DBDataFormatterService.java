package scorekeep.alex.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import scorekeep.alex.model.AuthorType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Alexander Pribytok
 * Date: 17.12.2020.
 */
@Service
public class DBDataFormatterService {

    private final DateTimeFormatter formatter;

    public DBDataFormatterService(@Value("${database.data.formatter.date:dd.MM.yyyy}") String dateFormat) {
        formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    public LocalDate toLocalDate(String date) {
        return LocalDate.parse(date, formatter);
    }
}
