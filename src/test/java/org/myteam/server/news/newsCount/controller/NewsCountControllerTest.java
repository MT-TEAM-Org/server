package org.myteam.server.news.newsCount.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.ControllerTestSupport;
import org.springframework.security.test.context.support.WithMockUser;

class NewsCountControllerTest extends ControllerTestSupport {

	@DisplayName("뉴스 추천을 추가한다.")
	@Test
	@WithMockUser
	void recommendTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				patch("/api/news/count/recommend/{newsId}", 1)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 추천 추가 성공"));
	}

	@DisplayName("뉴스 추천을 삭제한다.")
	@Test
	@WithMockUser
	void recommendCancelTest() throws Exception {
		// given
		// when // then
		mockMvc.perform(
				delete("/api/news/count/recommend/{newsId}", 1)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SUCCESS.name()))
			.andExpect(jsonPath("$.msg").value("뉴스 추천 삭제 성공"));
	}
}
