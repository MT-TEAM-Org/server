package org.myteam.server.news.newsComment.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.ControllerTestSupport;
import org.myteam.server.news.newsComment.dto.controller.request.NewsCommentRequest;
import org.myteam.server.news.newsComment.dto.controller.request.NewsCommentSaveRequest;
import org.myteam.server.news.newsComment.dto.controller.request.NewsCommentUpdateRequest;
import org.springframework.security.test.context.support.WithMockUser;

class NewsCommentControllerTest extends ControllerTestSupport {

	@DisplayName("뉴스 댓글을 저장한다.")
	@Test
	@WithMockUser
	void saveTest() throws Exception {
		// given
		NewsCommentSaveRequest newsCommentSaveRequest = NewsCommentSaveRequest.builder()
			.newsId(1L)
			.comment("댓글 테스트")
			.imgUrl("www.test.com")
			.build();

		// when // then
		mockMvc.perform(
				post("/api/news/comment")
					.content(objectMapper.writeValueAsString(newsCommentSaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 댓글 저장 성공"));
	}

	@DisplayName("뉴스 댓글을 저장시 뉴스ID는 필수이다.")
	@Test
	@WithMockUser
	void saveWithoutNewsIdTest() throws Exception {
		// given
		NewsCommentSaveRequest newsCommentSaveRequest = NewsCommentSaveRequest.builder()
			.comment("댓글 테스트")
			.imgUrl("www.test.com")
			.build();

		// when // then
		mockMvc.perform(
				post("/api/news/comment")
					.content(objectMapper.writeValueAsString(newsCommentSaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스ID는 필수입니다."));
	}

	@DisplayName("뉴스 댓글을 저장시 뉴스ID는 필수이다.")
	@Test
	@WithMockUser
	void saveWithoutCommentTest() throws Exception {
		// given
		NewsCommentSaveRequest newsCommentSaveRequest = NewsCommentSaveRequest.builder()
			.newsId(1L)
			.imgUrl("www.test.com")
			.build();

		// when // then
		mockMvc.perform(
				post("/api/news/comment")
					.content(objectMapper.writeValueAsString(newsCommentSaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 댓글은 필수입니다."));
	}

	@DisplayName("뉴스 댓글 목록을 조회한다.")
	@Test
	@WithMockUser
	void findAllTest() throws Exception {
		// given
		NewsCommentRequest newsCommentRequest = NewsCommentRequest.builder()
			.page(1)
			.size(10)
			.newsId(1L)
			.build();

		// when // then
		mockMvc.perform(
				get("/api/news/comment")
					.param("page", String.valueOf(newsCommentRequest.getPage()))
					.param("size", String.valueOf(newsCommentRequest.getSize()))
					.param("newsId", String.valueOf(newsCommentRequest.getNewsId()))
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 댓글 조회 성공"));
	}

	@DisplayName("뉴스목록을 조회시 뉴스ID는 필수이다.")
	@Test
	@WithMockUser
	void findAllWithoutOrderTypeTest() throws Exception {
		// given
		NewsCommentRequest newsRequest = NewsCommentRequest.builder()
			.page(1)
			.size(10)
			.build();

		// when // then
		mockMvc.perform(
				get("/api/news/comment")
					.param("page", String.valueOf(newsRequest.getPage()))
					.param("size", String.valueOf(newsRequest.getSize()))
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스ID는 필수입니다."));
	}

	@DisplayName("뉴스 댓글을 수정한다.")
	@Test
	@WithMockUser
	void updateTest() throws Exception {
		// given
		NewsCommentUpdateRequest newsCommentUpdateRequest = NewsCommentUpdateRequest.builder()
			.newsCommentId(1L)
			.comment("뉴스 댓글 테스트")
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/news/comment")
					.content(objectMapper.writeValueAsString(newsCommentUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 댓글 수정 성공"));
	}

	@DisplayName("뉴스 댓글을 수정시 뉴스댓글ID는 필수이다.")
	@Test
	@WithMockUser
	void updateWithoutCommentIdTest() throws Exception {
		// given
		NewsCommentUpdateRequest newsCommentUpdateRequest = NewsCommentUpdateRequest.builder()
			.comment("뉴스 댓글 테스트")
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/news/comment")
					.content(objectMapper.writeValueAsString(newsCommentUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 댓글 ID는 필수입니다."));
	}

	@DisplayName("뉴스 댓글을 수정시 댓글 내용은 필수이다.")
	@Test
	@WithMockUser
	void updateWithoutCommentTest() throws Exception {
		// given
		NewsCommentUpdateRequest newsCommentUpdateRequest = NewsCommentUpdateRequest.builder()
			.newsCommentId(1L)
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/news/comment")
					.content(objectMapper.writeValueAsString(newsCommentUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 댓글 내용은 필수입니다."));
	}

	@DisplayName("뉴스 댓글을 삭제한다.")
	@Test
	@WithMockUser
	void deleteTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				delete("/api/news/comment/{newsCommentId}", 1L)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 댓글 삭제 성공"));
	}
}
