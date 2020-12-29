package scorekeep.alex;

import lombok.Getter;
import scorekeep.alex.model.AuthorType;

/**
 * @author Alexander Pribytok
 * Date: 18.12.2020.
 */
@Getter
public class AuthorOperatingException extends RuntimeException {

    private final AuthorType authorType;
    private final Integer id;
    private final Operation operation;

    private AuthorOperatingException(AuthorType authorType, Integer id, Operation operation) {
        super("Error while dealing with an author.");
        this.authorType = authorType;
        this.id = id;
        this.operation = operation;
    }

    public static AuthorOperatingException whileGettingAnAuthors(AuthorType authorType) {
        return new AuthorOperatingException(authorType, null, Operation.GETTING_AUTHORS);
    }

    public static AuthorOperatingException whileGettingAnAuthor(AuthorType authorType, Integer id) {
        return new AuthorOperatingException(authorType, id, Operation.GETTING_AUTHOR);
    }

    public static AuthorOperatingException whileSavingAnAuthor(AuthorType authorType, Integer id) {
        return new AuthorOperatingException(authorType, id, Operation.SAVING);
    }

    public static AuthorOperatingException whileUpdatingAnAuthor(AuthorType authorType, Integer id) {
        return new AuthorOperatingException(authorType, id, Operation.UPDATING);
    }

    public static AuthorOperatingException whileDeletingAnAuthor(AuthorType authorType, Integer id) {
        return new AuthorOperatingException(authorType, id, Operation.DELETING);
    }

    private enum Operation {
        GETTING_AUTHOR, GETTING_AUTHORS, SAVING, UPDATING, DELETING
    }
}
