package org.myteam.server.news.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.page.response.PageableCustomResponse;
import org.myteam.server.news.domain.NewsCategory;
import org.myteam.server.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.news.dto.repository.NewsDto;
import org.myteam.server.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.repository.OrderType;
import org.springframework.beans.factory.annotation.Autowired;

class NewsReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsReadService newsReadService;

	@AfterEach
	void tearDown() {
		newsCountRepository.deleteAllInBatch();
		newsRepository.deleteAllInBatch();
	}

	@DisplayName("야구기사의 목록을 조회한다.")
	@Test
	void findAllBaseballTest() {
		createNews(1, NewsCategory.BASEBALL, 10);
		createNews(2, NewsCategory.BASEBALL, 14);
		createNews(3, NewsCategory.ESPORTS, 12);
		createNews(4, NewsCategory.BASEBALL, 12);

		NewsServiceRequest newsServiceRequest = NewsServiceRequest.builder()
			.category(NewsCategory.BASEBALL)
			.orderType(OrderType.LIKE)
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
				.extracting("title", "category", "thumbImg")
				.containsExactly(
					tuple("기사타이틀2", NewsCategory.BASEBALL, "www.test.com"),
					tuple("기사타이틀4", NewsCategory.BASEBALL, "www.test.com"),
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com")
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
			.orderType(OrderType.LIKE)
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
				.extracting("title", "category", "thumbImg")
				.containsExactly(
					tuple("기사타이틀6", NewsCategory.ESPORTS, "www.test.com"),
					tuple("기사타이틀5", NewsCategory.ESPORTS, "www.test.com"),
					tuple("기사타이틀7", NewsCategory.ESPORTS, "www.test.com"),
					tuple("기사타이틀4", NewsCategory.ESPORTS, "www.test.com")
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
			.orderType(OrderType.LIKE)
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
				.extracting("title", "category", "thumbImg")
				.containsExactly(
					tuple("기사타이틀11", NewsCategory.FOOTBALL, "www.test.com"),
					tuple("기사타이틀10", NewsCategory.FOOTBALL, "www.test.com"),
					tuple("기사타이틀9", NewsCategory.FOOTBALL, "www.test.com"),
					tuple("기사타이틀8", NewsCategory.FOOTBALL, "www.test.com")
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
			.orderType(OrderType.LIKE)
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
				.extracting("title", "category", "thumbImg")
				.containsExactly(
					tuple("기사타이틀3", NewsCategory.ESPORTS, "www.test.com"),
					tuple("기사타이틀2", NewsCategory.BASEBALL, "www.test.com"),
					tuple("기사타이틀4", NewsCategory.BASEBALL, "www.test.com"),
					tuple("기사타이틀1", NewsCategory.BASEBALL, "www.test.com")
				)
		);
	}


}
