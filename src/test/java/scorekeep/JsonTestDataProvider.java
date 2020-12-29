package scorekeep;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import scorekeep.alex.context.Caller;
import scorekeep.alex.dto.AuthorDto;
import scorekeep.alex.model.Author;
import scorekeep.alex.model.AuthorType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Alexander Pribytok
 * Date: 23.12.2020.
 */
public final class JsonTestDataProvider {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Author readAuthor(String json) throws IOException {
        File resource = new ClassPathResource(json).getFile();
        byte[] bytes = Files.readAllBytes(resource.toPath());
        AuthorDto dto = OBJECT_MAPPER.readValue(bytes, AuthorDto.class);
        return new Author(dto.getId(),
                dto.getFirstName(),
                dto.getLastName(),
                Caller.MOBILE_API.toLocalDateFromString(dto.getBirthday()),
                AuthorType.parseString(dto.getType()));
    }

    public static String readAsString(String json) throws IOException {
        File resource = new ClassPathResource(json).getFile();
        byte[] bytes = Files.readAllBytes(resource.toPath());
        return new String(bytes);
    }
}
