package scorekeep;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import scorekeep.alex.model.Author;
import scorekeep.alex.model.AuthorType;
import scorekeep.alex.repository.AuthorRepository;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Oleksandr Prybytok
 * Date: 21.12.2020.
 */
@ActiveProfiles("test")
@ExtendWith({AWSDynamodbDatabaseExtension.class, RestDocumentationExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AuthorAPITest {


    public static final String AUTHOR_TYPES = Stream.of(AuthorType.values()).map(Enum::name).collect(Collectors.joining(", "));
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthorRepository repository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    private MockMvc mvc;

    private DynamoDBMapper mapper;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withResponseDefaults(prettyPrint()))
                .build();
        //requestHeaders(headerWithName("Content-Type").description("The content type of the request is required."))

        mapper = new DynamoDBMapper(amazonDynamoDB);
        boolean empty = amazonDynamoDB.listTables().getTableNames().isEmpty();
        if (empty) {
            CreateTableRequest tableRequest = mapper.generateCreateTableRequest(Author.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            amazonDynamoDB.createTable(tableRequest);
            return;
        }
        mapper.batchDelete(mapper.scan(Author.class, new DynamoDBScanExpression()));

    }

    @Test
    public void shouldReturnAllAuthorsOfAuthorTypePassed() throws Exception {
        //GIVEN
        String expectedAuthor1 = JsonTestDataProvider.readAsString("scorekeep/AuthorAPITest/POET/author1.json");
        String expectedAuthor2 = JsonTestDataProvider.readAsString("scorekeep/AuthorAPITest/POET/author2.json");
        String bothAuthors = "[" + expectedAuthor1 + ", " + expectedAuthor2 + "]";
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/POET/author1.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/POET/author2.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/NOVELIST/author1.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/DETECTIVIST/author1.json"));

        //WHEN
        MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.get("/api/author/{type}",
                AuthorType.POET).contentType(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(bothAuthors))
                .andDo(document("get-authors-by-type",
                        pathParameters(parameterWithName("type").attributes().description("The type of the author. Values: " + AUTHOR_TYPES)),
                        requestHeaders(contentTypeHeader()),
                        responseFields(fieldWithPath("[]").description("An array of authors")).andWithPrefix("[].", authorFields())));
    }

    @Test
    public void shouldReturnPoetByTypeAndId() throws Exception {
        //GIVEN
        String expected = JsonTestDataProvider.readAsString("scorekeep/AuthorAPITest/POET/author1.json");
        Author author = JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/POET/author1.json");
        mapper.save(author);
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/POET/author2.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/NOVELIST/author1.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/DETECTIVIST/author1.json"));

        //WHEN
        MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.get("/api/author/{type}/{id}",
                author.getType().name(), author.getId()).contentType(MediaType.APPLICATION_JSON);
        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andDo(document("get-author",
                        pathParameters(getAnAuthorByTypeAndIdParameters()),
                        requestHeaders(contentTypeHeader()),
                        responseFields(authorFields()))
                );
    }

    @Test
    public void shouldReturn404IfAuthorHasntBeenFound() throws Exception {
        //GIVEN
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/POET/author1.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/NOVELIST/author1.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/DETECTIVIST/author1.json"));

        //WHEN
        MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.get("/api/author/{type}/{id}",
                AuthorType.POET, 1008811).contentType(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andDo(document("get-no-author-by-unknown-id",
                        pathParameters(getAnAuthorByTypeAndIdParameters()),
                        requestHeaders(contentTypeHeader())
                ));
    }

    @Test
    public void shouldReturn400IfAuthorTypeDoesntExist() throws Exception {
        //GIVEN
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/POET/author1.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/NOVELIST/author1.json"));
        mapper.save(JsonTestDataProvider.readAuthor("scorekeep/AuthorAPITest/DETECTIVIST/author1.json"));

        //WHEN
        mvc.perform(get("/api/author/PoEEEt/" + 1008811)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private HeaderDescriptor contentTypeHeader() {
        return headerWithName("Content-Type").description("The content type of the request is required.");
    }

    private ParameterDescriptor[] getAnAuthorByTypeAndIdParameters() {
        return new ParameterDescriptor[]{
                parameterWithName("type").attributes().description("The type of the author. Values: " + AUTHOR_TYPES),
                parameterWithName("id").attributes().description("An id of the author.")};
    }

    private FieldDescriptor[] authorFields() {
        return new FieldDescriptor[]{fieldWithPath("author_id").description("An id of the author."),
                fieldWithPath("type").description("An type of the author. The value corresponds to the requested type."),
                fieldWithPath("first_name").description("A first name of the author."),
                fieldWithPath("last_name").description("A last name of the author."),
                fieldWithPath("birthday").description("A birthday of the author.")};
    }
}
