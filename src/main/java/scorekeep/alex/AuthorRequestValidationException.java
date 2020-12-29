package scorekeep.alex;

import lombok.Getter;

/**
 * @author Alexander Pribytok
 * Date: 18.12.2020.
 */
@Getter
public class AuthorRequestValidationException extends RuntimeException {

    public AuthorRequestValidationException(String message) {
        super(message);
    }
}
