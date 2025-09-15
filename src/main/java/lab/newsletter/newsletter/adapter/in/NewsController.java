package lab.newsletter.newsletter.adapter.in;

import java.util.List;
import lab.newsletter.newsletter.application.NewsLetterService;
import lab.newsletter.newsletter.domain.NewsLetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NewsController {
    private final NewsLetterService service;
    // Home: search box(center) + full list(scroll)
    @GetMapping("/")
    public String home(@RequestParam(name = "query", required = false) String query,
                       @RequestParam(name = "sort", required = false, defaultValue = " ") String sort,
                       Model model) {

        List<NewsLetter> items =
                (query != null && !query.isBlank()) ? service.findByKeyword(query,sort) : service.findAll(sort);
        model.addAttribute("items", items);
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("sort", sort == null ? "" : sort);
        return "index";
    }

    // Optional list route -> reuse Home view
    @GetMapping("/news")
    public String list(@RequestParam(name = "q", required = false) String query, Model model) {
        return home(query, "",model);
    }

    // Detail page
    @GetMapping("/news/{id}")
    public String getNewsLetter(@PathVariable Integer id, Model model) {
        var item = service.get(id);
        model.addAttribute("item", item);
        return "news"; // templates/news.html
    }

}
