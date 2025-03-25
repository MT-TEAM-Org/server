package org.myteam.server.news.news.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.ControllerTestSupport;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.news.news.dto.controller.request.NewsRequest;
import org.myteam.server.news.news.repository.OrderType;
import org.springframework.security.test.context.support.WithMockUser;

class NewsControllerTest extends ControllerTestSupport {

	@DisplayName("뉴스목록을 조회한다.")
	@Test
	@WithMockUser
	void findAllTest() throws Exception {
		// given
		NewsRequest newsRequest = NewsRequest.builder()
			.page(1)
			.size(10)
			.search("테스트")
			.SearchType(BoardSearchType.CONTENT)
			.orderType(OrderType.DATE)
			.category(Category.FOOTBALL)
			.build();

		// when // then
		mockMvc.perform(
				get("/api/news")
					.param("search", "테스트")
					.param("orderType", newsRequest.getOrderType().name())
					.param("category", newsRequest.getCategory().name())
					.param("page", String.valueOf(newsRequest.getPage()))
					.param("size", String.valueOf(newsRequest.getSize()))
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 목록 조회 성공"));
	}

	@DisplayName("뉴스목록을 조회시 정렬 타입은 필수이다.")
	@Test
	@WithMockUser
	void findAllWithoutOrderTypeTest() throws Exception {
		// given
		NewsRequest newsRequest = NewsRequest.builder()
			.page(1)
			.size(10)
			.search("테스트")
			.category(Category.FOOTBALL)
			.build();

		// when // then
		mockMvc.perform(
				get("/api/news")
					.param("page", String.valueOf(newsRequest.getPage()))
					.param("size", String.valueOf(newsRequest.getSize()))
					.param("content", newsRequest.getSearch())
					.param("category", newsRequest.getCategory().name())
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 정렬 타입은 필수입니다."));
	}

	//	@DisplayName("뉴스목록을 조회시 뉴스 카테고리는 필수입니다.")
	//	@Test
	//	@WithMockUser
	//	void findAllWithoutCategoryTest() throws Exception {
	//		// given
	//		NewsRequest newsRequest = NewsRequest.builder()
	//			.page(1)
	//			.size(10)
	//			.content("테스트")
	//			.orderType(OrderType.DATE)
	//			.build();
	//
	//		// when // then
	//		mockMvc.perform(
	//				get("/api/news")
	//					.param("page", String.valueOf(newsRequest.getPage()))
	//					.param("size", String.valueOf(newsRequest.getSize()))
	//					.param("content", newsRequest.getContent())
	//					.param("orderType", newsRequest.getOrderType().name())
	//					.with(csrf())
	//			)
	//			.andDo(print())
	//			.andExpect(status().isBadRequest())
	//			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
	//			.andExpect(jsonPath("$.message").value("뉴스 카테고리는 필수입니다."));
	//	}

	@DisplayName("뉴스상세 조회를 한다.")
	@Test
	@WithMockUser
	void findOnetest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				get("/api/news/{newsId}", 1L)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 상세 조회 성공"));
	}
}
