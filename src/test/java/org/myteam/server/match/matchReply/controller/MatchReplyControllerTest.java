package org.myteam.server.match.matchReply.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.ControllerTestSupport;
import org.myteam.server.match.matchReply.dto.controller.request.MatchReplyRequest;
import org.myteam.server.match.matchReply.dto.controller.request.MatchReplySaveRequest;
import org.myteam.server.match.matchReply.dto.controller.request.MatchReplyUpdateRequest;
import org.springframework.security.test.context.support.WithMockUser;

class MatchReplyControllerTest extends ControllerTestSupport {

	@DisplayName("경기 대댓글을 저장한다.")
	@Test
	@WithMockUser
	void saveTest() throws Exception {
		// given
		MatchReplySaveRequest matchReplySaveRequest = MatchReplySaveRequest.builder()
			.matchCommentId(1L)
			.comment("대댓글 테스트")
			.build();

		// when // then
		mockMvc.perform(
				post("/api/match/reply")
					.content(objectMapper.writeValueAsString(matchReplySaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("경기 대댓글 저장 성공"));
	}

	@DisplayName("경기 대댓글을 저장시 경기ID는 필수이다.")
	@Test
	@WithMockUser
	void saveWithoutNewsIdTest() throws Exception {
		// given
		MatchReplySaveRequest matchReplySaveRequest = MatchReplySaveRequest.builder()
			.comment("대댓글 테스트")
			.build();

		// when // then
		mockMvc.perform(
				post("/api/match/reply")
					.content(objectMapper.writeValueAsString(matchReplySaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("경기 댓글 ID는 필수입니다."));
	}

	@DisplayName("경기 대댓글을 저장시 경기ID는 필수이다.")
	@Test
	@WithMockUser
	void saveWithoutCommentTest() throws Exception {
		// given
		MatchReplySaveRequest matchReplySaveRequest = MatchReplySaveRequest.builder()
			.matchCommentId(1L)
			.build();

		// when // then
		mockMvc.perform(
				post("/api/match/reply")
					.content(objectMapper.writeValueAsString(matchReplySaveRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("경기 대댓글은 필수입니다."));
	}

	@DisplayName("경기 대댓글 목록을 조회한다.")
	@Test
	@WithMockUser
	void findAllTest() throws Exception {
		// given
		MatchReplyRequest matchReplyRequest = MatchReplyRequest.builder()
			.page(1)
			.size(10)
			.matchCommentId(1L)
			.build();

		// when // then
		mockMvc.perform(
				get("/api/match/reply")
					.param("page", String.valueOf(matchReplyRequest.getPage()))
					.param("size", String.valueOf(matchReplyRequest.getSize()))
					.param("matchCommentId", String.valueOf(matchReplyRequest.getMatchCommentId()))
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("경기 대댓글 조회 성공"));
	}

	@DisplayName("대댓글 조회시 경기 대댓글 ID는 필수이다.")
	@Test
	@WithMockUser
	void findAllWithoutOrderTypeTest() throws Exception {
		// given
		MatchReplyRequest matchReplyRequest = MatchReplyRequest.builder()
			.page(1)
			.size(10)
			.build();

		// when // then
		mockMvc.perform(
				get("/api/match/reply")
					.param("page", String.valueOf(matchReplyRequest.getPage()))
					.param("size", String.valueOf(matchReplyRequest.getSize()))
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("경기 댓글 ID는 필수입니다."));
	}

	@DisplayName("경기 대댓글을 수정한다.")
	@Test
	@WithMockUser
	void updateTest() throws Exception {
		// given
		MatchReplyUpdateRequest matchReplyUpdateRequest = MatchReplyUpdateRequest.builder()
			.matchReplyId(1L)
			.comment("경기 대댓글 테스트")
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/match/reply")
					.content(objectMapper.writeValueAsString(matchReplyUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("경기 대댓글 수정 성공"));
	}

	@DisplayName("경기 대댓글을 수정시 경기대댓글ID는 필수이다.")
	@Test
	@WithMockUser
	void updateWithoutCommentIdTest() throws Exception {
		// given
		MatchReplyUpdateRequest matchReplyUpdateRequest = MatchReplyUpdateRequest.builder()
			.comment("경기 대댓글 테스트")
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/match/reply")
					.content(objectMapper.writeValueAsString(matchReplyUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("경기 대댓글 ID는 필수입니다."));
	}

	@DisplayName("경기 대댓글을 수정시 대댓글 내용은 필수이다.")
	@Test
	@WithMockUser
	void updateWithoutCommentTest() throws Exception {
		// given
		MatchReplyUpdateRequest matchReplyUpdateRequest = MatchReplyUpdateRequest.builder()
			.matchReplyId(1L)
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/match/reply")
					.content(objectMapper.writeValueAsString(matchReplyUpdateRequest))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.message").value("경기 대댓글 내용은 필수입니다."));
	}

	@DisplayName("경기 대댓글을 삭제한다.")
	@Test
	@WithMockUser
	void deleteTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				delete("/api/match/reply/{matchReplyId}", 1L)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("경기 대댓글 삭제 성공"));
	}
}
