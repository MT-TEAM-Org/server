package org.myteam.server.news.news.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.page.response.PageableCustomResponse;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.news.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.news.dto.service.response.NewsResponse;
import org.myteam.server.news.news.repository.OrderType;
import org.myteam.server.news.news.repository.TimePeriod;
import org.springframework.beans.factory.annotation.Autowired;

class NewsReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsReadService newsReadService;

	@DisplayName("야구기사의 목록을 조회한다.")
	@Test
	void findAllBaseballTest() {
		createNews(1, NewsCategory.BASEBALL, 10);
		createNews(2, NewsCategory.BASEBALL, 14);
		createNews(3, NewsCategory.ESPORTS, 12);
		createNews(4, NewsCategory.BASEBALL, 12);

		NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
			.category(NewsCategory.BASEBALL)
			.orderType(OrderType.DATE)
			.page(1)
			.size(10)
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
				.extracting("title", "category", "thumbImg", "content", "commentCount")
				.containsExactly(
					tuple("기사타이틀4", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 12),
					tuple("기사타이틀2", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 14),
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 10)
				)
		);
	}

	@DisplayName("ESports의 목록을 조회한다.")
	@Test
	void findAllEsportTest() {
		createNews(1, NewsCategory.BASEBALL, 10);
		createNews(2, NewsCategory.BASEBALL, 14);
		createNews(3, NewsCategory.BASEBALL, 12);
		createNews(4, NewsCategory.ESPORTS, 12);
		createNews(5, NewsCategory.ESPORTS, 20);
		createNews(6, NewsCategory.ESPORTS, 30);
		createNews(7, NewsCategory.ESPORTS, 15);

		NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
			.category(NewsCategory.ESPORTS)
			.orderType(OrderType.DATE)
			.page(1)
			.size(10)
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
					tuple("기사타이틀7", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 15),
					tuple("기사타이틀6", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 30),
					tuple("기사타이틀5", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 20),
					tuple("기사타이틀4", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 12)
				)
		);
	}

	@DisplayName("축구 목록을 조회한다.")
	@Test
	void findAllFootBallTest() {
		createNews(1, NewsCategory.BASEBALL, 10);
		createNews(2, NewsCategory.BASEBALL, 14);
		createNews(3, NewsCategory.BASEBALL, 12);
		createNews(4, NewsCategory.ESPORTS, 12);
		createNews(5, NewsCategory.ESPORTS, 20);
		createNews(6, NewsCategory.ESPORTS, 30);
		createNews(7, NewsCategory.ESPORTS, 15);
		createNews(8, NewsCategory.FOOTBALL, 11);
		createNews(9, NewsCategory.FOOTBALL, 12);
		createNews(10, NewsCategory.FOOTBALL, 13);
		createNews(11, NewsCategory.FOOTBALL, 14);

		NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
			.category(NewsCategory.FOOTBALL)
			.orderType(OrderType.DATE)
			.page(1)
			.size(10)
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
					tuple("기사타이틀11", NewsCategory.FOOTBALL, "www.test.com", "뉴스본문", 14),
					tuple("기사타이틀10", NewsCategory.FOOTBALL, "www.test.com", "뉴스본문", 13),
					tuple("기사타이틀9", NewsCategory.FOOTBALL, "www.test.com", "뉴스본문", 12),
					tuple("기사타이틀8", NewsCategory.FOOTBALL, "www.test.com", "뉴스본문", 11)
				)
		);
	}

	@DisplayName("카테고리가 없으면 전체 목록을 조회한다.")
	@Test
	void findAllTest() {
		createNews(1, NewsCategory.BASEBALL, 10);
		createNews(2, NewsCategory.BASEBALL, 14);
		createNews(3, NewsCategory.ESPORTS, 15);
		createNews(4, NewsCategory.BASEBALL, 12);

		NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
			.orderType(OrderType.DATE)
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
					1, 1, 4L
				),
			() -> assertThat(newsList)
				.extracting("title", "category", "thumbImg", "content", "commentCount")
				.containsExactly(
					tuple("기사타이틀4", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 12),
					tuple("기사타이틀3", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 15),
					tuple("기사타이틀2", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 14),
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 10)
				)
		);
	}

	@DisplayName("일별 전체 목록을 조회한다.")
	@Test
	void findAllDailyTest() {
		createNewsWithPostDate(1, NewsCategory.BASEBALL, 10, LocalDateTime.now().minusHours(1));
		createNewsWithPostDate(2, NewsCategory.BASEBALL, 14, LocalDateTime.now().minusDays(2));
		createNewsWithPostDate(3, NewsCategory.ESPORTS, 15, LocalDateTime.now().minusHours(1));
		createNewsWithPostDate(4, NewsCategory.BASEBALL, 12, LocalDateTime.now().minusDays(2));

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
					tuple("기사타이틀3", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 15),
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 10)
				)
		);
	}

	@DisplayName("주별 전체 목록을 조회한다.")
	@Test
	void findAllWeeklyTest() {
		createNewsWithPostDate(1, NewsCategory.BASEBALL, 10, LocalDateTime.now().minusDays(2));
		createNewsWithPostDate(2, NewsCategory.BASEBALL, 14, LocalDateTime.now().minusDays(7));
		createNewsWithPostDate(3, NewsCategory.ESPORTS, 15, LocalDateTime.now().minusDays(1));
		createNewsWithPostDate(4, NewsCategory.BASEBALL, 12, LocalDateTime.now().minusDays(7));

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
					tuple("기사타이틀3", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 15),
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 10)
				)
		);
	}

	@DisplayName("월별 전체 목록을 조회한다.")
	@Test
	void findAllMonthlyTest() {
		createNewsWithPostDate(1, NewsCategory.BASEBALL, 10, LocalDateTime.now().minusDays(10));
		createNewsWithPostDate(2, NewsCategory.BASEBALL, 14, LocalDateTime.now().minusMonths(1));
		createNewsWithPostDate(3, NewsCategory.ESPORTS, 15, LocalDateTime.now().minusDays(12));
		createNewsWithPostDate(4, NewsCategory.BASEBALL, 12, LocalDateTime.now().minusMonths(7));

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
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 10),
					tuple("기사타이틀3", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 15)
				)
		);
	}

	@DisplayName("년별 전체 목록을 조회한다.")
	@Test
	void findAllYearlyTest() {
		createNewsWithPostDate(1, NewsCategory.BASEBALL, 10, LocalDateTime.now().minusMonths(10));
		createNewsWithPostDate(2, NewsCategory.BASEBALL, 14, LocalDateTime.now().minusYears(1));
		createNewsWithPostDate(3, NewsCategory.ESPORTS, 15, LocalDateTime.now().minusMonths(11));
		createNewsWithPostDate(4, NewsCategory.BASEBALL, 12, LocalDateTime.now().minusYears(1));

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
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 10),
					tuple("기사타이틀3", NewsCategory.ESPORTS, "www.test.com", "뉴스본문", 15)
				)
		);
	}

	@DisplayName("제목으로 목록을 조회한다.")
	@Test
	void findAllWithContentTest() {
		createNews(1, NewsCategory.BASEBALL, 10);
		createNews(2, NewsCategory.BASEBALL, 14);
		createNews(3, NewsCategory.ESPORTS, 15);
		createNews(4, NewsCategory.BASEBALL, 12);

		NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
			.orderType(OrderType.DATE)
			.content("타이틀1")
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
				.extracting("title", "category", "thumbImg", "content", "commentCount")
				.containsExactly(
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com", "뉴스본문", 10)
				)
		);
	}

	@DisplayName("뉴스 상세 조회를 한다.")
	@Test
	void findOneTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		createNews(2, NewsCategory.BASEBALL, 14);
		createNews(3, NewsCategory.ESPORTS, 15);
		createNews(4, NewsCategory.BASEBALL, 12);

		NewsResponse newsResponse = newsReadService.findOne(news.getId());

		assertThat(newsResponse)
			.extracting("title", "category", "thumbImg", "recommendCount", "commentCount", "viewCount", "source",
				"content")
			.contains("기사타이틀1", NewsCategory.BASEBALL, "www.test.com", 10, 10, 10, "www.test.com", "뉴스본문");
	}

}
