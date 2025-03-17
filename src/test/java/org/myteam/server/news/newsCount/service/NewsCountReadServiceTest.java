package org.myteam.server.news.newsCount.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCountReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsCountReadService newsCountReadService;

	@DisplayName("뉴스 카운트를 조회한다.")
	@Test
	void findByIdTest() {
		News news = createNews(1, Category.FOOTBALL, 1);

		NewsCount newsCount = newsCountReadService.findByNewsId(news.getId());

		assertThat(newsCount).isNotNull();
	}
}
