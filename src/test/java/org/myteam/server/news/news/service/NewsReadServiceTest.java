package org.myteam.server.news.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.page.response.PageableCustomResponse;
import org.myteam.server.global.util.domain.TimePeriod;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.news.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.news.dto.service.response.NewsResponse;
import org.myteam.server.news.news.repository.OrderType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;

class NewsReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private NewsReadService newsReadService;

    @DisplayName("야구기사의 목록을 조회한다.")
    @Test
    void findAllBaseballTest() {
        createNews(1, Category.BASEBALL, 10);
        createNews(2, Category.BASEBALL, 14);
        createNews(3, Category.ESPORTS, 12);
        createNews(4, Category.BASEBALL, 12);

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .category(Category.BASEBALL)
                .orderType(OrderType.DATE)
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.ALL)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertThat(pageInfo)
                .extracting("currentPage", "totalPage", "totalElement")
                .containsExactlyInAnyOrder(
                        1, 1, 3L
                );

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 3L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount", "isHot")
                        .containsExactly(
                                tuple("기사타이틀4", Category.BASEBALL, "www.test.com", "뉴스본문", 12, true),
                                tuple("기사타이틀2", Category.BASEBALL, "www.test.com", "뉴스본문", 14, true),
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 10, true)
                        )
        );
    }

    @DisplayName("ESports의 목록을 조회한다.")
    @Test
    void findAllEsportTest() {
        createNews(1, Category.BASEBALL, 10);
        createNews(2, Category.BASEBALL, 14);
        createNews(3, Category.BASEBALL, 12);
        createNews(4, Category.ESPORTS, 12);
        createNews(5, Category.ESPORTS, 20);
        createNews(6, Category.ESPORTS, 30);
        createNews(7, Category.ESPORTS, 15);

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .category(Category.ESPORTS)
                .orderType(OrderType.DATE)
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.ALL)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertThat(pageInfo)
                .extracting("currentPage", "totalPage", "totalElement")
                .containsExactlyInAnyOrder(
                        1, 1, 4L
                );

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 4L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount")
                        .containsExactly(
                                tuple("기사타이틀7", Category.ESPORTS, "www.test.com", "뉴스본문", 15),
                                tuple("기사타이틀6", Category.ESPORTS, "www.test.com", "뉴스본문", 30),
                                tuple("기사타이틀5", Category.ESPORTS, "www.test.com", "뉴스본문", 20),
                                tuple("기사타이틀4", Category.ESPORTS, "www.test.com", "뉴스본문", 12)
                        )
        );
    }

    @DisplayName("축구 목록을 조회한다.")
    @Test
    void findAllFootBallTest() {
        createNews(1, Category.BASEBALL, 10);
        createNews(2, Category.BASEBALL, 14);
        createNews(3, Category.BASEBALL, 12);
        createNews(4, Category.ESPORTS, 12);
        createNews(5, Category.ESPORTS, 20);
        createNews(6, Category.ESPORTS, 30);
        createNews(7, Category.ESPORTS, 15);
        createNews(8, Category.FOOTBALL, 11);
        createNews(9, Category.FOOTBALL, 12);
        createNews(10, Category.FOOTBALL, 13);
        createNews(11, Category.FOOTBALL, 14);

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .category(Category.FOOTBALL)
                .orderType(OrderType.DATE)
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.ALL)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertThat(pageInfo)
                .extracting("currentPage", "totalPage", "totalElement")
                .containsExactlyInAnyOrder(
                        1, 1, 4L
                );

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 4L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount")
                        .containsExactly(
                                tuple("기사타이틀11", Category.FOOTBALL, "www.test.com", "뉴스본문", 14),
                                tuple("기사타이틀10", Category.FOOTBALL, "www.test.com", "뉴스본문", 13),
                                tuple("기사타이틀9", Category.FOOTBALL, "www.test.com", "뉴스본문", 12),
                                tuple("기사타이틀8", Category.FOOTBALL, "www.test.com", "뉴스본문", 11)
                        )
        );
    }

    @DisplayName("카테고리가 없으면 전체 목록을 조회한다.")
    @Test
    void findAllTest() {
        createNews(1, Category.BASEBALL, 10);
        createNews(2, Category.BASEBALL, 14);
        createNews(3, Category.ESPORTS, 15);
        createNews(4, Category.BASEBALL, 12);

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .orderType(OrderType.DATE)
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.ALL)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 4L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount")
                        .containsExactly(
                                tuple("기사타이틀4", Category.BASEBALL, "www.test.com", "뉴스본문", 12),
                                tuple("기사타이틀3", Category.ESPORTS, "www.test.com", "뉴스본문", 15),
                                tuple("기사타이틀2", Category.BASEBALL, "www.test.com", "뉴스본문", 14),
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 10)
                        )
        );
    }

    @DisplayName("추천수와, 조회수가 합해서 상위 10개는 HotNews이다.")
    @Test
    void findAllWithHotTest() {
        createNews(1, Category.BASEBALL, 1);
        createNews(2, Category.BASEBALL, 2);
        createNews(3, Category.ESPORTS, 3);
        createNews(4, Category.BASEBALL, 4);
        createNews(5, Category.BASEBALL, 5);
        createNews(6, Category.BASEBALL, 6);
        createNews(7, Category.BASEBALL, 7);
        createNews(8, Category.BASEBALL, 8);
        createNews(9, Category.BASEBALL, 9);
        createNews(10, Category.BASEBALL, 10);
        createNews(11, Category.BASEBALL, 11);

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .orderType(OrderType.DATE)
                .page(1)
                .size(11)
                .timePeriod(TimePeriod.ALL)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 11L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount", "isHot")
                        .containsExactly(
                                tuple("기사타이틀11", Category.BASEBALL, "www.test.com", "뉴스본문", 11, true),
                                tuple("기사타이틀10", Category.BASEBALL, "www.test.com", "뉴스본문", 10, true),
                                tuple("기사타이틀9", Category.BASEBALL, "www.test.com", "뉴스본문", 9, true),
                                tuple("기사타이틀8", Category.BASEBALL, "www.test.com", "뉴스본문", 8, true),
                                tuple("기사타이틀7", Category.BASEBALL, "www.test.com", "뉴스본문", 7, true),
                                tuple("기사타이틀6", Category.BASEBALL, "www.test.com", "뉴스본문", 6, true),
                                tuple("기사타이틀5", Category.BASEBALL, "www.test.com", "뉴스본문", 5, true),
                                tuple("기사타이틀4", Category.BASEBALL, "www.test.com", "뉴스본문", 4, true),
                                tuple("기사타이틀3", Category.ESPORTS, "www.test.com", "뉴스본문", 3, true),
                                tuple("기사타이틀2", Category.BASEBALL, "www.test.com", "뉴스본문", 2, true),
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 1, false)
                        )
        );
    }

    @DisplayName("일별 전체 목록을 조회한다.")
    @Test
    void findAllDailyTest() {
        createNewsWithPostDate(1, Category.BASEBALL, 10, LocalDateTime.now().minusHours(1));
        createNewsWithPostDate(2, Category.BASEBALL, 14, LocalDateTime.now().minusDays(2));
        createNewsWithPostDate(3, Category.ESPORTS, 15, LocalDateTime.now().minusHours(1));
        createNewsWithPostDate(4, Category.BASEBALL, 12, LocalDateTime.now().minusDays(2));

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .orderType(OrderType.DATE)
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.DAILY)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 2L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount")
                        .containsExactly(
                                tuple("기사타이틀3", Category.ESPORTS, "www.test.com", "뉴스본문", 15),
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 10)
                        )
        );
    }

    @DisplayName("주별 전체 목록을 조회한다.")
    @Test
    void findAllWeeklyTest() {
        createNewsWithPostDate(1, Category.BASEBALL, 10, LocalDateTime.now().minusDays(2));
        createNewsWithPostDate(2, Category.BASEBALL, 14, LocalDateTime.now().minusDays(7));
        createNewsWithPostDate(3, Category.ESPORTS, 15, LocalDateTime.now().minusDays(1));
        createNewsWithPostDate(4, Category.BASEBALL, 12, LocalDateTime.now().minusDays(7));

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .orderType(OrderType.DATE)
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.WEEKLY)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 2L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount")
                        .containsExactly(
                                tuple("기사타이틀3", Category.ESPORTS, "www.test.com", "뉴스본문", 15),
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 10)
                        )
        );
    }

    @DisplayName("월별 전체 목록을 조회한다.")
    @Test
    void findAllMonthlyTest() {
        createNewsWithPostDate(1, Category.BASEBALL, 10, LocalDateTime.now().minusDays(10));
        createNewsWithPostDate(2, Category.BASEBALL, 14, LocalDateTime.now().minusMonths(1));
        createNewsWithPostDate(3, Category.ESPORTS, 15, LocalDateTime.now().minusDays(12));
        createNewsWithPostDate(4, Category.BASEBALL, 12, LocalDateTime.now().minusMonths(7));

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .orderType(OrderType.DATE)
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.MONTHLY)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 2L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount")
                        .containsExactly(
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 10),
                                tuple("기사타이틀3", Category.ESPORTS, "www.test.com", "뉴스본문", 15)
                        )
        );
    }

    @DisplayName("년별 전체 목록을 조회한다.")
    @Test
    void findAllYearlyTest() {
        createNewsWithPostDate(1, Category.BASEBALL, 10, LocalDateTime.now().minusMonths(10));
        createNewsWithPostDate(2, Category.BASEBALL, 14, LocalDateTime.now().minusYears(1));
        createNewsWithPostDate(3, Category.ESPORTS, 15, LocalDateTime.now().minusMonths(11));
        createNewsWithPostDate(4, Category.BASEBALL, 12, LocalDateTime.now().minusYears(1));

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .orderType(OrderType.DATE)
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.YEARLY)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 2L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount")
                        .containsExactly(
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 10),
                                tuple("기사타이틀3", Category.ESPORTS, "www.test.com", "뉴스본문", 15)
                        )
        );
    }

    @DisplayName("제목으로 목록을 조회한다.")
    @Test
    void findAllWithContentTest() {
        createNews(1, Category.BASEBALL, 10);
        createNews(2, Category.BASEBALL, 14);
        createNews(3, Category.ESPORTS, 15);
        createNews(4, Category.BASEBALL, 12);

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .orderType(OrderType.DATE)
                .searchType(BoardSearchType.TITLE)
                .search("타이틀1")
                .page(1)
                .size(10)
                .timePeriod(TimePeriod.ALL)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 1L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount")
                        .containsExactly(
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 10)
                        )
        );
    }

    @DisplayName("뉴스 상세 조회를 한다.")
    @Test
    void findOneTest() {
        News news = createNews(1, Category.BASEBALL, 10);
        createNews(2, Category.BASEBALL, 14);
        createNews(3, Category.ESPORTS, 15);
        createNews(4, Category.BASEBALL, 12);

        given(redisCountService.getViewCountAndIncr(eq(DomainType.NEWS), anyLong()))
                .willReturn(10);
        NewsResponse newsResponse = newsReadService.findOne(news.getId());

        assertThat(newsResponse)
                .extracting("title", "category", "thumbImg", "recommendCount", "commentCount", "viewCount", "source",
                        "content")
                .contains("기사타이틀1", Category.BASEBALL, "www.test.com", 10, 10, 10, "www.test.com", "뉴스본문");
    }

    @DisplayName("뉴스 댓글 조회시 댓글이 정상 조회 되는지 확인한다.")
    @Test
    void findWithComment() {
        News news = createNews(1, Category.BASEBALL, 10);
        Member member = createMember(1);
        createNewsComment(news, member, "테스트댓글");
        createNews(2, Category.BASEBALL, 14);
        createNews(3, Category.ESPORTS, 15);
        createNews(4, Category.BASEBALL, 12);

        NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
                .orderType(OrderType.DATE)
                .searchType(BoardSearchType.COMMENT)
                .search("테스트")
                .timePeriod(TimePeriod.ALL)
                .page(1)
                .size(10)
                .build();

        NewsListResponse newsListResponse = newsReadService.findAll(newsServiceRequest);

        List<NewsDto> newsList = newsListResponse.getList().getContent();
        PageableCustomResponse pageInfo = newsListResponse.getList().getPageInfo();

        assertAll(
                () -> assertThat(pageInfo)
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactlyInAnyOrder(
                                1, 1, 1L
                        ),
                () -> assertThat(newsList)
                        .extracting("title", "category", "thumbImg", "content", "commentCount",
                                "newsCommentSearchDto.comment", "newsCommentSearchDto.imageUrl")
                        .containsExactly(
                                tuple("기사타이틀1", Category.BASEBALL, "www.test.com", "뉴스본문", 10, "테스트댓글", "www.test.com")
                        )
        );
    }

}
