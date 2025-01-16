package org.myteam.server.article.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.article.controller.response.ArticleSaveResponse;
import org.myteam.server.article.dto.ArticleSaveRequest;
import org.myteam.server.article.entity.Type;
import org.myteam.server.article.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleServiceTest extends IntegrationTestSupport {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleRepository articleRepository;

    @AfterEach
    void tearDown() {
        articleRepository.deleteAllInBatch();
    }

    @DisplayName("기사를 저장한다.")
    @Test
    void saveTest() {
        ArticleSaveRequest request = ArticleSaveRequest.builder()
                .title("기사타이틀")
                .type(Type.BASEBALL)
                .thumbImgUrl("www.test.com")
                .description("기사의 상세내용")
                .postDate(LocalDateTime.of(2025,1,1,1,1))
                .build();

        ArticleSaveResponse articleSaveResponse = articleService.save(request);

        assertThat(articleRepository.findById(articleSaveResponse.getId()).get())
                .extracting("title", "type", "thumbImgUrl", "description", "postDate")
                .containsExactly("기사타이틀", Type.BASEBALL, "www.test.com", "기사의 상세내용", LocalDateTime.of(2025,1,1,1,1));
    }
}
