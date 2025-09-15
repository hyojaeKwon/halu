package lab.newsletter.newsletter.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lab.newsletter.newsletter.adapter.out.NewsHtmlConverter;
import lab.newsletter.newsletter.domain.NewsLetter;
import lab.newsletter.newsletter.domain.NewsLetterItem;
import lab.newsletter.newsletter.application.port.out.NewsLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsLetterService {

    private final NewsHtmlConverter converter;
    private final NewsLetterRepository repo;
    private final HitsUpUseCase hitsUpUseCase;

    public List<NewsLetter> findAll(String sortType) {
        List<NewsLetter> newses = new java.util.ArrayList<>(
                repo.findAll().stream().map(NewsLetterItem::toDomain).toList());
        List<NewsLetter> sortedNews = defaultSort(sortType, newses);
        return converter.newsListConvert(sortedNews.stream().toList());
    }

    // specific
    public NewsLetter get(Integer letterId) {
        NewsLetter news = repo.getById(letterId).toDomain();
        news = news.upHits();
        hitsUpUseCase.uploadNews(news);
        return converter.newsConvert(news);
    }

    public List<NewsLetter> findByKeyword(String keyword, String sortType) {
        List<NewsLetter> newses = repo.findAll().stream().map(NewsLetterItem::toDomain).toList();
        List<NewsLetter> sortedNews = defaultSort(sortType, newses);
        return keywordTokenizationAndSearch(keyword, sortedNews);
    }

    // 단어 넣으면 List에서 비슷한 단어 추출 (토큰화를)
    private List<NewsLetter> keywordTokenizationAndSearch(String keyword, List<NewsLetter> newses) {
        if (keyword.isEmpty() || keyword.isBlank()) {
            return newses;
        }
        Set<String> tokens = Arrays.stream(keyword.split("\\s+")).map(String::toLowerCase).collect(Collectors.toSet());
        return newses.stream()
                .filter(news -> tokens.stream().anyMatch(token -> news.getKeyword().toLowerCase().contains(token)))
                .distinct().toList();
    }

    // sort By what?
    private List<NewsLetter> defaultSort(String sortType, List<NewsLetter> newses) {
        List<NewsLetter> sortTargetList = new ArrayList<>(newses);

        Comparator<NewsLetter> byCreatedDesc = Comparator.comparing(NewsLetter::getCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        Comparator<NewsLetter> byHitsDesc = (news1, news2) -> {
            int cmp = Integer.compare(news2.getHits(), news1.getHits());
            if (cmp == 0) {
                return news2.getCreatedAt().compareTo(news1.getCreatedAt());
            }
            return cmp;
        };

        if (sortType.equalsIgnoreCase("hits")) {
            return sortTargetList.stream().sorted(byHitsDesc).toList();
        }
        return sortTargetList.stream().sorted(byCreatedDesc).toList();

    }

    // paging 처리

}
