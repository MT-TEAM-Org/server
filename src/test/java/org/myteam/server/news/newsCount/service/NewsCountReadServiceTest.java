package org.myteam.server.news.newsCount.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.TestContainerSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCountReadServiceTest extends TestContainerSupport {

    @Autowired
    private NewsCountReadService newsCountReadService;

    private News news;

    @DisplayName("뉴스 카운트를 조회한다.")
    @Test
    void findByIdTest() {
        News news = createNews(1, Category.FOOTBALL, 1);

        NewsCount newsCount = newsCountReadService.findByNewsId(news.getId());

        assertThat(newsCount).isNotNull();
    }

    @Test
    @DisplayName("1. 뉴스 카운트 조회 성공")
    void findByNewsId_success() {
        // given
        news = createNews(100, Category.BASEBALL, 8);

        // when
        NewsCount found = newsCountReadService.findByNewsId(news.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getNews().getId()).isEqualTo(news.getId());
    }

    @Test
    @DisplayName("2. 뉴스 카운트 조회 실패 시 예외 발생")
    void findByNewsId_fail() {
        // given
        news = createNews(100, Category.BASEBALL, 8);

        // when & then
        assertThatThrownBy(() -> newsCountReadService.findByNewsId(9999L))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.NEWS_COUNT_NOT_FOUND.getMsg());
    }
}
