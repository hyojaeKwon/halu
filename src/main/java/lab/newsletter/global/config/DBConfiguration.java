package lab.newsletter.global.config;

import java.net.URI;
import java.time.Duration;
import lab.newsletter.newsletter.domain.NewsLetterItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Configuration
public class DBConfiguration {

    @Value("${aws.dynamodb.endpoint:}")
    private String endpoint;

    @Value("${aws.dynamodb.table}")
    private String tableName;

    @Bean
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient ddb) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
    }

    @Bean
    public DynamoDbTable<NewsLetterItem> newsLetterTable(DynamoDbEnhancedClient client) {
        return client.table(tableName, TableSchema.fromBean(NewsLetterItem.class));
    }

    @Bean
    public DynamoDbClient dynamoDbClientProd() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(defaultOverride());

        if (endpoint != null && !endpoint.isBlank()) {
            builder = builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    private ClientOverrideConfiguration defaultOverride() {
        return ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofSeconds(10))
                .apiCallAttemptTimeout(Duration.ofSeconds(5))
                .build();
    }
}
