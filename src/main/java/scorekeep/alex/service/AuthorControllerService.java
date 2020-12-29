package scorekeep.alex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import scorekeep.alex.AuthorOperatingException;
import scorekeep.alex.AuthorRequestValidationException;
import scorekeep.alex.context.Caller;
import scorekeep.alex.context.ServiceAPIContext;
import scorekeep.alex.dto.AuthorDto;
import scorekeep.alex.model.Author;
import scorekeep.alex.model.AuthorType;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexander Pribytok
 * Date: 16.12.2020.
 */
@Service
public class AuthorControllerService {

    private final static Logger logger = LoggerFactory.getLogger(AuthorControllerService.class);

    @Autowired
    public AuthorService authorService;

    public Collection<AuthorDto> getAuthors(String typeArg) {
        validateAuthorType(typeArg);
        AuthorType type = AuthorType.parseString(typeArg);
        try {
            Collection<Author> authors = authorService.getAuthorsByType(type);
            return authors.stream().map(this::convertAuthor).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while getting authors: TYPE={}.", type, e);
            throw AuthorOperatingException.whileGettingAnAuthors(type);
        }
    }

    public AuthorDto getAuthor(String typeArg, String idArg) {
        validateAuthorId(idArg);
        validateAuthorType(typeArg);
        AuthorType type = AuthorType.parseString(typeArg);
        int id = Integer.parseInt(idArg);
        try {
            Author author = authorService.getAuthor(type, id);
            return convertAuthor(author);
        } catch (Exception e) {
            logger.error("Error while getting an author: TYPE={}, ID={}.", type, id, e);
            throw AuthorOperatingException.whileGettingAnAuthor(type, id);
        }
    }

    public void save(String typeArg, String idArg, AuthorDto dto) {
        validateAuthorId(idArg);
        validateAuthorType(typeArg);
        validateAuthor(dto);
        AuthorType type = AuthorType.parseString(typeArg);
        int id = Integer.parseInt(idArg);
        Author newAuthor = new Author();
        newAuthor.setId(id);
        newAuthor.setType(type);
        newAuthor.setFirstName(dto.getFirstName());
        newAuthor.setLastName(dto.getLastName());
        ServiceAPIContext context = new ServiceAPIContext(Caller.MOBILE_API);
        newAuthor.setBirthday(context.getCaller().toLocalDateFromString(dto.getBirthday()));
        try {
            authorService.save(newAuthor);
        } catch (Exception e) {
            logger.error("Error while saving an author: TYPE={}, ID={}, FIELDS={}.", type, id, newAuthor, e);
            throw AuthorOperatingException.whileSavingAnAuthor(type, id);
        }
    }

    public void update(String typeArg, String idArg, AuthorDto dto) {
        validateAuthorId(idArg);
        validateAuthorType(typeArg);
        validateAuthor(dto);
        AuthorType type = AuthorType.parseString(typeArg);
        int id = Integer.parseInt(idArg);
        Author updated = new Author();
        updated.setId(id);
        updated.setType(type);
        updated.setFirstName(dto.getFirstName());
        updated.setLastName(dto.getLastName());
        ServiceAPIContext context = new ServiceAPIContext(Caller.MOBILE_API);
        updated.setBirthday(context.getCaller().toLocalDateFromString(dto.getBirthday()));
        try {
            authorService.update(updated);
        } catch (Exception e) {
            logger.error("Error while updating an author: TYPE={}, ID={}, FIELDS={}.", type, id, updated, e);
            throw AuthorOperatingException.whileUpdatingAnAuthor(type, id);
        }
    }

    public void delete(String typeArg, String idArg) {
        validateAuthorId(idArg);
        validateAuthorType(typeArg);
        AuthorType type = AuthorType.parseString(typeArg);
        int id = Integer.parseInt(idArg);
        try {
            authorService.delete(type, id);
        } catch (Exception e) {
            logger.error("Error while deleting an author: TYPE={}, ID={}.", type, id, e);
            throw AuthorOperatingException.whileDeletingAnAuthor(type, id);
        }
    }

    private void validateAuthor(AuthorDto dto) {
        if (StringUtils.isEmpty(dto.getBirthday())) {
            throw new AuthorRequestValidationException("Birthday can't be empty.");
        }
        if (StringUtils.isEmpty(dto.getFirstName())) {
            throw new AuthorRequestValidationException("'FirstName' can't be empty.");
        }
        if (StringUtils.isEmpty(dto.getLastName())) {
            throw new AuthorRequestValidationException("'LastName' can't be empty.");
        }
    }

    private void validateAuthorType(String type) {
        Objects.requireNonNull(type);
        if (StringUtils.isEmpty(type)) {
            throw new AuthorRequestValidationException("An author's type can't be null.");
        }
        try {
            AuthorType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new AuthorRequestValidationException("An author's type value " + type + " isn't supported.");
        }
    }

    private void validateAuthorId(String idArg) {
        Objects.requireNonNull(idArg);
        if (StringUtils.isEmpty(idArg)) {
            throw new AuthorRequestValidationException("An id can't be empty.");
        }
        int id = Integer.parseInt(idArg);
        if (id < 0) {
            throw new AuthorRequestValidationException("An id can't be less then 0.");
        }
    }

    private AuthorDto convertAuthor(Author author) {
        if (author == null) {
            return null;
        }
        ServiceAPIContext context = new ServiceAPIContext(Caller.MOBILE_API);
        LocalDate birthday = author.getBirthday();
        String birthdayFormatted = context.getCaller().formatLocalDateToString(birthday);
        return new AuthorDto(author.getId(), author.getFirstName(), author.getLastName(), birthdayFormatted,
                author.getType().toString());
    }
}
