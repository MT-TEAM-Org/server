package org.myteam.server.article.controller.response;

import lombok.Builder;
import lombok.Getter;
import org.myteam.server.article.entity.Article;

import java.time.LocalDateTime;

@Getter
public class ArticleSaveResponse {
    private Long id;

    private String title;

    private String thumbImgUrl;

    private String description;

    private LocalDateTime postDate;

    @Builder
    public ArticleSaveResponse(Long id, String title, String thumbImgUrl, String description, LocalDateTime postDate) {
        this.id = id;
        this.title = title;
        this.thumbImgUrl = thumbImgUrl;
        this.description = description;
        this.postDate = postDate;
    }

    public static ArticleSaveResponse of(Article article) {
        return ArticleSaveResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .thumbImgUrl(article.getThumbImgUrl())
                .description(article.getDescription())
                .postDate(article.getPostDate())
                .build();
    }
}
