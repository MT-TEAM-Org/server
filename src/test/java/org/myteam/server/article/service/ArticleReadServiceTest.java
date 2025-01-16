package org.myteam.server.article.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.article.entity.Article;
import org.myteam.server.article.entity.Type;
import org.myteam.server.article.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class ArticleReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private ArticleReadService articleReadService;
    @Autowired
    private ArticleRepository articleRepository;

    @AfterEach
    void tearDown() {
        articleRepository.deleteAllInBatch();
    }

    @DisplayName("야구기사의 목록을 조회한다.")
    @Test
    void findAllBaseballTest() {
        articleRepository.save(createArticle(1, Type.BASEBALL));
        articleRepository.save(createArticle(2, Type.BASEBALL));
        articleRepository.save(createArticle(3, Type.ESPORTS));
        articleRepository.save(createArticle(4, Type.BASEBALL));

        assertThat(articleReadService.findAll(Type.BASEBALL))
                .extracting("title", "type", "thumbImgUrl", "description", "postDate")
                .containsExactly(
                        tuple("기사타이틀1", Type.BASEBALL, "www.test.com", "기사의 상세내용1", LocalDateTime.of(2025, 1, 1, 1, 1)),
                        tuple("기사타이틀2", Type.BASEBALL, "www.test.com", "기사의 상세내용2", LocalDateTime.of(2025, 1, 1, 1, 1)),
                        tuple("기사타이틀4", Type.BASEBALL, "www.test.com", "기사의 상세내용4", LocalDateTime.of(2025, 1, 1, 1, 1))
                );
    }

    @DisplayName("기사의 상세데이터를 조회한다.")
    @Test
    void findByIdTest() {
        Article savedArticle = articleRepository.save(createArticle(1, Type.BASEBALL));

        assertThat(articleReadService.findById(savedArticle.getId()))
                .extracting("title", "type", "thumbImgUrl", "description", "postDate")
                .containsExactly("기사타이틀1", Type.BASEBALL, "www.test.com", "기사의 상세내용1", LocalDateTime.of(2025, 1, 1, 1, 1));
    }

    private Article createArticle(int index, Type type) {
        return Article.builder()
                .title("기사타이틀" + index)
                .type(type)
                .thumbImgUrl("www.test.com")
                .description("기사의 상세내용" + index)
                .postDate(LocalDateTime.of(2025, 1, 1, 1, 1))
                .build();
    }
}
