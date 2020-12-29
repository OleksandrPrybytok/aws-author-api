package scorekeep;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexander Pribytok
 * Date: 21.12.2020.
 */
@Configuration
public class AWSDynamoDBConfiguration {

    final static Logger logger = LoggerFactory.getLogger(AWSDynamoDBConfiguration.class);

    @Bean
    public AmazonDynamoDB amazonDynamoDB(@Autowired DynamoDBConfig config) {
        logger.trace("Loaded the Dynamodb config {}.", config);
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder
                .EndpointConfiguration(config.getDBUrl(), config.region);
        BasicAWSCredentials credentials = new BasicAWSCredentials(config.accessKey, config.secretKey);
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(new ClientConfiguration().withRequestTimeout(5000))
                .build();
    }

    @Data
    @Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "storage.aws")
    public class DynamoDBConfig {
        private String url;
        private int port;
        private String region;
        private String accessKey;
        private String secretKey;

        private String getDBUrl() {
            return url + ":" + port;
        }
    }
}
