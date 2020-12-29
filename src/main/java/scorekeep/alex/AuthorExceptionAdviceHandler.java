package scorekeep.alex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Alexander Pribytok
 * Date: 18.12.2020.
 */
@ControllerAdvice
public class AuthorExceptionAdviceHandler {

    private final static Logger logger = LoggerFactory.getLogger(AuthorExceptionAdviceHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<String> employeeNotFoundHandler(Exception ex) {
        logger.error("Exception throw while operating with Author.", ex);
        if (ex instanceof AuthorOperatingException) {
            AuthorOperatingException authorOperatingException = (AuthorOperatingException) ex;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    String.format("An error has been thrown while operating with an author. " +
                            "Operation type: %s.", authorOperatingException.getOperation()));
        } else if (ex instanceof AuthorRequestValidationException) {
            AuthorRequestValidationException authorRequestValidationException = (AuthorRequestValidationException) ex;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Validation error: " + authorRequestValidationException.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error while performing an operation. Please, " +
                "contact the support team or try later.");
    }
}
