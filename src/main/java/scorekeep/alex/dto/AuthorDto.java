package scorekeep.alex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Alexander Pribytok
 * Date: 16.12.2020.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public final class AuthorDto {

    @JsonProperty("author_id")
    private final Integer id;

    @JsonProperty("first_name")
    private final String firstName;

    @JsonProperty("last_name")
    private final String lastName;

    @JsonProperty("birthday")
    private final String birthday;

    @JsonProperty("type")
    private String type;

    @JsonCreator
    public static AuthorDto creator(
            @JsonProperty("author_id") Integer authorId,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("birthday") String birthday,
            @JsonProperty("type") String type) {

        return new AuthorDto(authorId, firstName, lastName, birthday, type);
    }
}
