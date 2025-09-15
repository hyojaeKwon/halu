package lab.newsletter.newsletter.application;

import java.time.LocalDateTime;
import lab.newsletter.newsletter.adapter.out.NewsHtmlConverter;
import lab.newsletter.newsletter.application.port.out.NewsLetterRepository;
import lab.newsletter.newsletter.domain.NewsLetter;
import lab.newsletter.newsletter.domain.NewsLetterItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsLetterServiceTest {

    @Mock
    private NewsHtmlConverter converter;
    @Mock
    private NewsLetterRepository repo;
    @Mock
    private HitsUpUseCase hitsUpUseCase;

    @InjectMocks
    private NewsLetterService newsLetterService;

    private NewsLetterItem item1, item2;
    private NewsLetter domain1, domain2;

    private final LocalDateTime date = LocalDateTime.of(2025, 9, 25, 14, 40);

    @BeforeEach
    void setUp() {
        // 테스트를 위한 기본 데이터 설정
        item1 = new NewsLetterItem(1, "New Jeans is best", "newjeans, idol", date, "newjeans", 100, 0);
        item2 = new NewsLetterItem(2, "Le Sserafim is good", "lesserafim, idol", date, "lesserafim", 200, 10);

        domain1 = item1.toDomain();
        domain2 = item2.toDomain();

        lenient().when(converter.newsListConvert(any())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(converter.newsConvert(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("findAll 호출 시, 레포지토리에서 모든 데이터를 가져와 날짜 내림차순으로 정렬하여 반환한다")
    void findAll_shouldSortByDateDesc_byDefault() {
        // Given
        when(repo.findAll()).thenReturn(Arrays.asList(item1, item2));

        // When
        List<NewsLetter> result = newsLetterService.findAll("");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(item1.getId()); // item1이 최신 날짜
        assertThat(result.get(1).getId()).isEqualTo(item2.getId());
        verify(repo).findAll();
    }

    @Test
    @DisplayName("findAll을 'hits'로 정렬 시, 조회수 내림차순으로 정렬하여 반환한다")
    void findAll_shouldSortByHitsDesc() {
        // Given
        when(repo.findAll()).thenReturn(Arrays.asList(item1, item2));

        // When
        List<NewsLetter> result = newsLetterService.findAll("hits");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(item2.getId()); // item2가 조회수 더 높음
        assertThat(result.get(1).getId()).isEqualTo(item1.getId());
    }

    @Test
    @DisplayName("get 호출 시, ID에 해당하는 데이터를 가져오고 조회수를 1 올리고 저장 로직을 호출한다")
    void get_shouldIncreaseHitsAndSave() {
        // Given
        when(repo.getById(anyInt())).thenReturn(item1);
        doNothing().when(hitsUpUseCase).uploadNews(any(NewsLetter.class));

        // When
        NewsLetter result = newsLetterService.get(1);

        // Then
        verify(repo).getById(1);
        verify(hitsUpUseCase).uploadNews(any(NewsLetter.class));
        assertThat(result.getHits()).isEqualTo(domain1.getHits() + 1);
    }

    @Test
    @DisplayName("findByKeyword 호출 시, 키워드에 맞는 데이터를 필터링하여 반환한다")
    void findByKeyword_shouldFilterByKeyword() {
        // Given
        when(repo.findAll()).thenReturn(Arrays.asList(item1, item2));

        // When
        List<NewsLetter> result = newsLetterService.findByKeyword("newjeans", "");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(item1.getId());
    }
}
