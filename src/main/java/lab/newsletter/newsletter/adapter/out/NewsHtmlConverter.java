package lab.newsletter.newsletter.adapter.out;

import java.util.List;
import lab.newsletter.newsletter.domain.NewsLetter;
import lab.newsletter.global.util.HtmlMdConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsHtmlConverter {

    private final HtmlMdConverter htmlMdConverter;

    public List<NewsLetter> newsListConvert(List<NewsLetter> newsLetters) {
        return newsLetters.stream().map(i -> i.convertContentToHTML(htmlMdConverter.toHtml(i.getContent()))).toList();
    }

    public NewsLetter newsConvert(NewsLetter newsLetter) {
        return newsLetter.convertContentToHTML(htmlMdConverter.toHtml(newsLetter.getContent()));
    }

}
