package org.myteam.server.news.newsReply.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.ControllerTestSupport;
import org.myteam.server.news.newsReply.dto.controller.request.NewsReplyRequest;
import org.myteam.server.news.newsReply.dto.controller.request.NewsReplySaveRequest;
import org.myteam.server.news.newsReply.dto.controller.request.NewsReplyUpdateRequest;
import org.springframework.security.test.context.support.WithMockUser;

class NewsReplyControllerTest extends ControllerTestSupport {

	@DisplayName("뉴스 대댓글을 저장한다.")
	@Test
	@WithMockUser
	void saveTest() throws Exception {
		// given
		NewsReplySaveRequest newsReplySaveRequest = NewsReplySaveRequest.builder()
			.newsCommentId(1L)
			.comment("대댓글 테스트")
			.imgUrl("www.test.com")
			.mentionedPublicId(UUID.randomUUID())
			.build();

		// when // then
		mockMvc.perform(
				post("/api/news/reply")
					.content(objectMapper.writeValueAsString(newsReplySaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 대댓글 저장 성공"));
	}

	@DisplayName("뉴스 대댓글을 저장시 뉴스ID는 필수이다.")
	@Test
	@WithMockUser
	void saveWithoutNewsIdTest() throws Exception {
		// given
		NewsReplySaveRequest newsReplySaveRequest = NewsReplySaveRequest.builder()
			.comment("대댓글 테스트")
			.imgUrl("www.test.com")
			.mentionedPublicId(UUID.randomUUID())
			.build();

		// when // then
		mockMvc.perform(
				post("/api/news/reply")
					.content(objectMapper.writeValueAsString(newsReplySaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 댓글 ID는 필수입니다."));
	}

	@DisplayName("뉴스 대댓글을 저장시 뉴스ID는 필수이다.")
	@Test
	@WithMockUser
	void saveWithoutCommentTest() throws Exception {
		// given
		NewsReplySaveRequest newsReplySaveRequest = NewsReplySaveRequest.builder()
			.newsCommentId(1L)
			.imgUrl("www.test.com")
			.mentionedPublicId(UUID.randomUUID())
			.build();

		// when // then
		mockMvc.perform(
				post("/api/news/reply")
					.content(objectMapper.writeValueAsString(newsReplySaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 대댓글은 필수입니다."));
	}

	@DisplayName("뉴스 대댓글 목록을 조회한다.")
	@Test
	@WithMockUser
	void findAllTest() throws Exception {
		// given
		NewsReplyRequest newsReplyRequest = NewsReplyRequest.builder()
			.page(1)
			.size(10)
			.newsCommentId(1L)
			.build();

		// when // then
		mockMvc.perform(
				get("/api/news/reply")
					.param("page", String.valueOf(newsReplyRequest.getPage()))
					.param("size", String.valueOf(newsReplyRequest.getSize()))
					.param("newsCommentId", String.valueOf(newsReplyRequest.getNewsCommentId()))
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 대댓글 조회 성공"));
	}

	@DisplayName("대댓글 조회시 뉴스 대댓글 ID는 필수이다.")
	@Test
	@WithMockUser
	void findAllWithoutOrderTypeTest() throws Exception {
		// given
		NewsReplyRequest newsRequest = NewsReplyRequest.builder()
			.page(1)
			.size(10)
			.build();

		// when // then
		mockMvc.perform(
				get("/api/news/reply")
					.param("page", String.valueOf(newsRequest.getPage()))
					.param("size", String.valueOf(newsRequest.getSize()))
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 댓글 ID는 필수입니다."));
	}

	@DisplayName("뉴스 대댓글을 수정한다.")
	@Test
	@WithMockUser
	void updateTest() throws Exception {
		// given
		NewsReplyUpdateRequest newsCommentUpdateRequest = NewsReplyUpdateRequest.builder()
			.newsReplyId(1L)
			.comment("뉴스 대댓글 테스트")
			.mentionedPublicId(UUID.randomUUID())
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/news/reply")
					.content(objectMapper.writeValueAsString(newsCommentUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 대댓글 수정 성공"));
	}

	@DisplayName("뉴스 대댓글을 수정시 뉴스대댓글ID는 필수이다.")
	@Test
	@WithMockUser
	void updateWithoutCommentIdTest() throws Exception {
		// given
		NewsReplyUpdateRequest newsCommentUpdateRequest = NewsReplyUpdateRequest.builder()
			.comment("뉴스 대댓글 테스트")
			.mentionedPublicId(UUID.randomUUID())
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/news/reply")
					.content(objectMapper.writeValueAsString(newsCommentUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 대댓글 ID는 필수입니다."));
	}

	@DisplayName("뉴스 대댓글을 수정시 대댓글 내용은 필수이다.")
	@Test
	@WithMockUser
	void updateWithoutCommentTest() throws Exception {
		// given
		NewsReplyUpdateRequest newsCommentUpdateRequest = NewsReplyUpdateRequest.builder()
			.newsReplyId(1L)
			.mentionedPublicId(UUID.randomUUID())
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/news/reply")
					.content(objectMapper.writeValueAsString(newsCommentUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("뉴스 대댓글 내용은 필수입니다."));
	}

	@DisplayName("뉴스 대댓글을 삭제한다.")
	@Test
	@WithMockUser
	void deleteTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				delete("/api/news/reply/{newsReplyId}", 1L)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 대댓글 삭제 성공"));
	}

	@DisplayName("뉴스 대댓글 추천을 추가한다.")
	@Test
	@WithMockUser
	void recommendTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				patch("/api/news/reply/recommend/{newsCommentId}", 1)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 대댓글 추천 성공"));
	}

	@DisplayName("뉴스 추천을 삭제한다.")
	@Test
	@WithMockUser
	void recommendCancelTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				delete("/api/news/reply/recommend/{newsCommentId}", 1)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 대댓글 추천 삭제 성공"));
	}
}
