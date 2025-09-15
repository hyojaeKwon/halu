package lab.newsletter.newsletter.application.port.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lab.newsletter.newsletter.domain.NewsLetterItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
@RequiredArgsConstructor
public class NewsLetterRepository {
    private final DynamoDbTable<NewsLetterItem> table;

    public NewsLetterItem getById(Integer id) {
        return table.getItem(Key.builder().partitionValue(id).build());
    }

    public List<NewsLetterItem> findAll() {
        List<NewsLetterItem> all = new ArrayList<>();
        table.scan().items().forEach(all::add);
        return all;
    }

    public List<NewsLetterItem> findByKeywordScan(String keyword) {
        List<NewsLetterItem> results = new ArrayList<>();
        table.scan(r -> r.filterExpression(Expression.builder().expression("keyword = :kw")
                        .expressionValues(Map.of(":kw", AttributeValue.builder().s(keyword).build())).build())).items()
                .forEach(results::add);
        return results;
    }

    public void save(NewsLetterItem newsLetterItem) {
        table.updateItem(newsLetterItem);
    }

    public record PageResult(java.util.List<NewsLetterItem> items, String nextSortKey) {
    }
}
