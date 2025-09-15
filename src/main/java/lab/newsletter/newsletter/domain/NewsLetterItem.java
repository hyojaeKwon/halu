package lab.newsletter.newsletter.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.time.format.DateTimeFormatter;

@DynamoDbBean
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsLetterItem {
    @Setter
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String keyword;
    private Integer sectionId;
    private Integer hits;

    public static NewsLetterItem fromDomain(NewsLetter newsLetter) {
        return new NewsLetterItem(
                newsLetter.getId(), newsLetter.getTitle(), newsLetter.getContent(), newsLetter.getCreatedAt(), newsLetter.getKeyword(), null,
                newsLetter.getHits()
        );
    }

    @DynamoDbPartitionKey
    public Integer getId() {
        return this.id;
    }

//    @DynamoDbSortKey
    @DynamoDbConvertedBy(LocalDateTimeConverter.class)
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public NewsLetter toDomain() {
        return NewsLetter.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .keyword(this.keyword)
                .createdAt(this.createdAt)
                .hits(this.hits == null ? 0 : this.hits)
                .build();
    }

    // DynamoDB Enhanced Client Converter for LocalDateTime <-> String (flexible formats)
    public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime> {
        // Write in ISO-8601 with 'T' consistently
        private static final DateTimeFormatter WRITE_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Candidate readers (no offset)
        private static final DateTimeFormatter[] READ_FMTS = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,                       // 2025-09-10T16:46:22[.SSS]
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),         // 2025-09-10 16:46:22
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),     // 2025-09-10 16:46:22.123
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),       // 2025-09-10T16:46:22
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")    // 2025-09-10T16:46:22.123
        };

        @Override
        public AttributeValue transformFrom(LocalDateTime input) {
            if (input == null) return null; // omit attribute when null (DynamoDB-friendly)
            return AttributeValue.builder().s(WRITE_FMT.format(input)).build();
        }

        @Override
        public LocalDateTime transformTo(AttributeValue attributeValue) {
            if (attributeValue == null || (attributeValue.nul() != null && attributeValue.nul())) {
                return null;
            }
            String s = attributeValue.s();
            if (s == null || s.isBlank()) return null;

            // 1) Try LocalDateTime candidates (no offset)
            for (DateTimeFormatter f : READ_FMTS) {
                try { return LocalDateTime.parse(s, f); } catch (Exception ignore) {}
            }

            // 2) Try ISO_OFFSET_DATE_TIME (e.g., 2025-09-10T16:46:22+09:00 or with 'Z')
            try { return java.time.OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime(); } catch (Exception ignore) {}

            // 3) Try Instant (e.g., 2025-09-10T07:46:22Z)
            try { return java.time.Instant.parse(s).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(); } catch (Exception ignore) {}

            // Fallback: explicit error for easier debugging
            throw new java.time.format.DateTimeParseException("Unsupported datetime format", s, 0);
        }

        @Override
        public EnhancedType<LocalDateTime> type() {
            return EnhancedType.of(LocalDateTime.class);
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.S;
        }
    }
}
