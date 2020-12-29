package scorekeep.alex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scorekeep.alex.dto.AuthorDto;
import scorekeep.alex.service.AuthorControllerService;

import java.util.Collection;

/**
 * @author Alexander Pribytok
 * Date: 16.12.2020.
 */
@RestController
@RequestMapping(path = "/api/author/{type}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorController {

    private final static Logger logger = LoggerFactory.getLogger(AuthorController.class);

    @Autowired
    private AuthorControllerService authorControllerService;

    @GetMapping
    public ResponseEntity<Collection<AuthorDto>> getAuthors(@PathVariable String type) {
        Collection<AuthorDto> authors = authorControllerService.getAuthors(type);
        return authors.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable String type, @PathVariable String id) {
        AuthorDto author = authorControllerService.getAuthor(type, id);
        return author != null ? ResponseEntity.ok(author) : ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{id}")
    public void save(@RequestBody AuthorDto dto, @PathVariable String type, @PathVariable String id) {
        authorControllerService.save(type, id, dto);
    }

    @PutMapping("/{id}")
    public void update(@RequestBody AuthorDto dto, @PathVariable String type, @PathVariable String id) {
        authorControllerService.update(type, id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String type, @PathVariable String id) {
        authorControllerService.delete(type, id);
    }
}
