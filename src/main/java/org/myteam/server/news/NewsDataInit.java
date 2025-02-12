package org.myteam.server.news;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.ChatRoom;
import org.myteam.server.chat.domain.FilterData;
import org.myteam.server.chat.repository.ChatRoomRepository;
import org.myteam.server.chat.repository.FilterDataRepository;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.repository.NewsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsDataInit implements CommandLineRunner {

    private NewsRepository newsRepository;

    @Override
    public void run(String... args) throws Exception {
        News news1 = News.builder()
                .category(NewsCategory.BASEBALL)
                .title("타이트을1")
                .thumbImg(null)
                .postDate(LocalDateTime.now().minusDays(1))
                .build();
        News news2 = News.builder()
                .category(NewsCategory.ESPORTS)
                .title("타이트을2")
                .thumbImg(null)
                .postDate(LocalDateTime.now().minusDays(2))
                .build();
        News news3 = News.builder()
                .category(NewsCategory.FOOTBALL)
                .title("타이트을3")
                .thumbImg(null)
                .postDate(LocalDateTime.now().minusDays(3))
                .build();


        newsRepository.save(news1);
        newsRepository.save(news2);
        newsRepository.save(news3);

        log.info("뉴스 데이터 저장 완료");
    }

}
