package org.myteam.server.matchSchedule.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.ControllerTestSupport;
import org.myteam.server.match.matchSchedule.domain.MatchCategory;
import org.springframework.security.test.context.support.WithMockUser;

class MatchScheduleControllerTest extends ControllerTestSupport {

	@DisplayName("뉴스목록을 조회한다.")
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
}
