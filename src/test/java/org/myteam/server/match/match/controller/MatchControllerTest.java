package org.myteam.server.match.match.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.ControllerTestSupport;
import org.myteam.server.match.match.domain.MatchCategory;
import org.springframework.security.test.context.support.WithMockUser;

class MatchControllerTest extends ControllerTestSupport {

	@DisplayName("경기목록 조회한다.")
	@Test
	@WithMockUser
	void findSchedulesBetweenDateTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				get("/api/match/schedule/{matchCategory}", MatchCategory.FOOTBALL)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("경기 일정 조회 성공"));
	}

	@DisplayName("경기를 상세 조회한다.")
	@Test
	@WithMockUser
	void findById() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				get("/api/match/{matchId}", 1L)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("경기 상세 조회 성공"));
	}
}
