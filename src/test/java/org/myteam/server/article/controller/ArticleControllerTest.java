package org.myteam.server.article.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.ControllerTestSupport;
import org.myteam.server.article.entity.Type;
import org.springframework.security.test.context.support.WithMockUser;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArticleControllerTest extends ControllerTestSupport {

    @DisplayName("기사목록을 조회한다.")
    @Test
    @WithMockUser
    void findAllTest() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        get("/api/article/list/{type}", Type.BASEBALL)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SUCCESS.name()))
                .andExpect(jsonPath("$.msg").value("기사 목록 조회 성공"));
    }

    @DisplayName("기사목록을 조회한다.")
    @Test
    @WithMockUser
    void findByIdTest() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        get("/api/article/{articleId}", 1)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SUCCESS.name()))
                .andExpect(jsonPath("$.msg").value("기사 상세 조회 성공"));
    }
}
