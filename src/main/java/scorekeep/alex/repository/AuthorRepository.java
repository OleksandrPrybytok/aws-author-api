package scorekeep.alex.repository;

import scorekeep.alex.model.Author;
import scorekeep.alex.model.AuthorType;

import java.util.Collection;

/**
 * @author Alexander Pribytok
 * Date: 16.12.2020.
 */
public interface AuthorRepository {

    Author getAuthorByType(AuthorType authorType, int id);

    Collection<Author> getAuthorByType(AuthorType authorType);

    void save(Author author);

    void update(Author author);

    void delete(AuthorType authorType, int id);
}
