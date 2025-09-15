package lab.newsletter.newsletter.adapter.in;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lab.newsletter.newsletter.application.NewsLetterService;
import lab.newsletter.newsletter.domain.NewsLetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class NewsControllerTest {

    private final LocalDateTime date = LocalDateTime.of(2025, 9, 25, 14, 40);
    private MockMvc mockMvc;
    @Mock
    private NewsLetterService newsLetterService;

    @BeforeEach
    void setup() {
        NewsController controller = new NewsController(newsLetterService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("메인 페이지(/)를 요청하면 index 뷰와 뉴스레터 목록을 반환한다")
    void home_shouldReturnIndexViewWithItems() throws Exception {
        // Given: 서비스가 반환할 가짜 데이터 설정
        NewsLetter newsLetter = new NewsLetter(1, "Test Title", "Test Content", date, "title", "http://example.com",
                0);
        List<NewsLetter> newsLetters = Collections.singletonList(newsLetter);
        when(newsLetterService.findAll(anyString())).thenReturn(newsLetters);

        // When & Then: 실제 요청을 보내고 결과를 검증
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"))
                .andExpect(model().attributeExists("items")).andExpect(model().attribute("items", newsLetters));
    }

    @Test
    @DisplayName("뉴스레터 상세 페이지(/news/{id})를 요청하면 news 뷰와 해당 뉴스레터 정보를 반환한다")
    void getNewsLetter_shouldReturnNewsViewWithItem() throws Exception {
        // Given
        int newsId = 1;
        NewsLetter newsLetter = new NewsLetter(newsId, "Test Title", "Test Content", date, "title",
                "http://example.com", 0);
        when(newsLetterService.get(anyInt())).thenReturn(newsLetter);

        // When & Then
        mockMvc.perform(get("/news/{id}", newsId)).andExpect(status().isOk()).andExpect(view().name("news"))
                .andExpect(model().attributeExists("item")).andExpect(model().attribute("item", newsLetter));
    }

    @Test
    @DisplayName("검색어와 함께 메인 페이지를 요청하면 필터링된 목록을 반환한다")
    void home_withQuery_shouldReturnFilteredItems() throws Exception {
        // Given
        String query = "Test";
        NewsLetter newsLetter = new NewsLetter(1, "Test Title", "Test Content", date, "title", "http://example.com",
                0);
        List<NewsLetter> filteredList = Collections.singletonList(newsLetter);
        when(newsLetterService.findByKeyword(anyString(), anyString())).thenReturn(filteredList);

        // When & Then
        mockMvc.perform(get("/").param("query", query)).andExpect(status().isOk()).andExpect(view().name("index"))
                .andExpect(model().attribute("items", filteredList)).andExpect(model().attribute("query", query));
    }
}
