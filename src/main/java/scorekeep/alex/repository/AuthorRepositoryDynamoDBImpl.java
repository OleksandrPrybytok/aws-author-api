package scorekeep.alex.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import scorekeep.alex.model.Author;
import scorekeep.alex.model.AuthorType;

import java.time.LocalDate;
import java.util.Collection;

/**
 * @author Alexander Pribytok
 * Date: 16.12.2020.
 */
@Repository
public class AuthorRepositoryDynamoDBImpl implements AuthorRepository {

    //    private final Table table;
    private final DBDataFormatterService formatterService;
    private final DynamoDBMapper mapper;
    private final DynamoDBMapperConfig mapperConfig;

    @Autowired
    public AuthorRepositoryDynamoDBImpl(AmazonDynamoDB amazonDynamoDB,
                                        DBDataFormatterService formatterService) {
//        this.table = new DynamoDB(amazonDynamoDB).getTable(AuthorTableMetaData.getTableName());
        this.formatterService = formatterService;
        this.mapper = new DynamoDBMapper(amazonDynamoDB);

        DynamoDBMapperConfig.TableNameOverride tableNameOverride = new DynamoDBMapperConfig
                .TableNameOverride(AuthorTableMetaData.getTableName());
        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        builder.setTableNameOverride(tableNameOverride);
        mapperConfig = builder.build();
    }

    @Override
    public Author getAuthorByType(AuthorType authorType, int id) {
        return mapper.load(Author.ofHashKeyValueAndRangeKey(authorType, id), mapperConfig);
    }

    @Override
    public Collection<Author> getAuthorByType(AuthorType authorType) {
        DynamoDBQueryExpression<Author> expression = new DynamoDBQueryExpression<Author>()
                .withHashKeyValues(Author.ofHashKeyValue(authorType));
        return mapper.query(Author.class, expression);
    }

    @Override
    public void save(Author author) {
        mapper.save(author, mapperConfig);
    }

    @Override
    public void update(Author author) {
        mapper.save(author);
    }

    @Override
    public void delete(AuthorType authorType, int id) {
        mapper.delete(Author.ofHashKeyValueAndRangeKey(authorType, id));
    }

    private Author itemToAuthor(Item item) {
        int id = item.getInt(AuthorTableMetaData.AUTHOR_ID.attribute);
        String firstName = item.getString(AuthorTableMetaData.FIRST_NAME.attribute);
        String lastName = item.getString(AuthorTableMetaData.LAST_NAME.attribute);
        LocalDate birthday = formatterService.toLocalDate(item.getString(AuthorTableMetaData.BIRTHDAY.attribute));
        AuthorType type = AuthorType.parseString(item.getString(AuthorTableMetaData.TYPE.attribute));
        return new Author(id, firstName, lastName, birthday, type);
    }

    public enum AuthorTableMetaData {
        AUTHOR_ID("author_id"), FIRST_NAME("first_name"), LAST_NAME
                ("last_name"), BIRTHDAY("birthday"), TYPE("type");
        private final String attribute;

        AuthorTableMetaData(String attribute) {
            this.attribute = attribute;
        }

        public static String getTableName() {
            return "AuthorTable";
        }
    }
}
