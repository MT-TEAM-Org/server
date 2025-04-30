package org.myteam.server.match.matchPrediction.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.ControllerTestSupport;
import org.myteam.server.match.matchPrediction.dto.controller.MatchPredictionRequest;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.springframework.security.test.context.support.WithMockUser;

class MatchPredictionControllerTest extends ControllerTestSupport {

	@DisplayName("경기 예측 현황을 조회한다.")
	@Test
	@WithMockUser
	void findOneTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				get("/api/match/prediction/{matchId}", 1L)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("경기예측 현황 조회 성공"));
	}

	@DisplayName("경기예측을 저장한다.")
	@Test
	@WithMockUser
	void updateTest() throws Exception {
		// given
		MatchPredictionRequest request = MatchPredictionRequest.builder()
			.matchPredictionId(1L)
			.side(Side.HOME)
			.build();

		// when // then
		mockMvc.perform(
				patch("/api/match/prediction")
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.accept(APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("경기예측 저장 성공"));
	}
}
