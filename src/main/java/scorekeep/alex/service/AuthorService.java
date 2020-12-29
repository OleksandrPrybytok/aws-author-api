package scorekeep.alex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scorekeep.alex.model.Author;
import scorekeep.alex.model.AuthorType;
import scorekeep.alex.repository.AuthorRepository;

import java.util.Collection;

/**
 * @author Alexander Pribytok
 * Date: 16.12.2020.
 */
@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public Author getAuthor(AuthorType type, int id) {
        return authorRepository.getAuthorByType(type, id);
    }

    public Collection<Author> getAuthorsByType(AuthorType type) {
        return authorRepository.getAuthorByType(type);
    }

    public void update(Author author) {
        authorRepository.update(author);
    }

    public void save(Author author) {
        authorRepository.save(author);
    }

    public void delete(AuthorType authorType, int id) {
        authorRepository.delete(authorType, id);
    }
}
