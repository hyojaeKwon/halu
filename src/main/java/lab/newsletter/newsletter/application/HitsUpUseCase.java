package lab.newsletter.newsletter.application;

import lab.newsletter.newsletter.domain.NewsLetter;
import lab.newsletter.newsletter.domain.NewsLetterItem;
import lab.newsletter.newsletter.application.port.out.NewsLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HitsUpUseCase {

    private final NewsLetterRepository repo;

    @Async
    public void uploadNews(NewsLetter news) {
        repo.save(NewsLetterItem.fromDomain(news));
    }
}
