package scorekeep.alex.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author Alexander Pribytok
 * Date: 16.12.2020.
 */
@DynamoDBTable(tableName = "AuthorTable")
public class Author {

    @DynamoDBRangeKey(attributeName = "author_id")
    private Integer id;

    @DynamoDBAttribute(attributeName = "first_name")
    private String firstName;

    @DynamoDBAttribute(attributeName = "last_name")
    private String lastName;

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute(attributeName = "birthday")
    private LocalDate birthday;

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    @DynamoDBHashKey(attributeName = "type")
    private AuthorType type;

    public Author() {
    }

    public Author(Integer id, String firstName, String lastName, LocalDate birthday, AuthorType type) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.type = type;
    }

    public static Author ofHashKeyValue(AuthorType authorType) {
        Author author = new Author();
        author.setType(authorType);
        return author;
    }

    public static Author ofHashKeyValueAndRangeKey(AuthorType authorType, int id) {
        Author author = new Author();
        author.setType(authorType);
        author.setId(id);
        return author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public AuthorType getType() {
        return type;
    }

    public void setType(AuthorType type) {
        this.type = type;
    }

    static public class LocalDateConverter implements DynamoDBTypeConverter<String, LocalDate> {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        @Override
        public String convert(LocalDate object) {
            return formatter.format(object);
        }

        @Override
        public LocalDate unconvert(String birthday) {
            return LocalDate.parse(birthday, formatter);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id) && Objects.equals(firstName, author.firstName)
                && Objects.equals(lastName, author.lastName) && Objects.equals(birthday, author.birthday) && type == author.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, birthday, type);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                ", type=" + type +
                '}';
    }
}
