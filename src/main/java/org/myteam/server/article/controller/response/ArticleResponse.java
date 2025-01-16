package org.myteam.server.article.controller.response;

import lombok.Builder;
import lombok.Getter;
import org.myteam.server.article.entity.Article;
import org.myteam.server.article.entity.Type;

import java.time.LocalDateTime;

@Getter
public class ArticleResponse {
    private Long id;

    private Type type;

    private String title;

    private String thumbImgUrl;

    private String description;

    private LocalDateTime postDate;

    @Builder
    public ArticleResponse(Long id, Type type, String title, String thumbImgUrl, String description, LocalDateTime postDate) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.thumbImgUrl = thumbImgUrl;
        this.description = description;
        this.postDate = postDate;
    }

    public static ArticleResponse of(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .type(article.getType())
                .title(article.getTitle())
                .thumbImgUrl(article.getThumbImgUrl())
                .description(article.getDescription())
                .postDate(article.getPostDate())
                .build();
    }
}
