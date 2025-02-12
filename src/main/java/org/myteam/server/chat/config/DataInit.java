package org.myteam.server.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.ChatRoom;
import org.myteam.server.chat.repository.ChatRoomRepository;
import org.myteam.server.chat.domain.FilterData;
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
public class DataInit implements CommandLineRunner {

    private final ChatRoomRepository repository;
    private final FilterDataRepository filterDataRepository;
    private final NewsRepository newsRepository;

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

        ChatRoom chatRoom1 = new ChatRoom("맨유 VS 토트넘");
        ChatRoom chatRoom2 = new ChatRoom("아스날 VS 맨시티");
        ChatRoom chatRoom3 = new ChatRoom("첼시 VS 리버풀");

        repository.save(chatRoom1);
        repository.save(chatRoom2);
        repository.save(chatRoom3);

        FilterData filterData1 = new FilterData("맹구");
        FilterData filterData2 = new FilterData("닭트넘");

        filterDataRepository.save(filterData1);
        filterDataRepository.save(filterData2);

        log.info("데이터 초기화 완료");
    }
}
