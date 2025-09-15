package lab.newsletter.newsletter.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class NewsLetter {
    private final int id;
    private final String sectionName;
    private final String keyword;
    private final LocalDateTime createdAt;
    private final String title;
    private final String content;
    private final int hits;

    public NewsLetter convertContentToHTML(String convertedContent) {
        return toBuilder().content(convertedContent).build();
    }

    public NewsLetter upHits() {
        return toBuilder().hits(this.hits + 1).build();
    }
}
