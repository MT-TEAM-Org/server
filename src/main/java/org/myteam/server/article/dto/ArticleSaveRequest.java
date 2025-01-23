package org.myteam.server.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.article.entity.Article;
import org.myteam.server.article.entity.Type;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ArticleSaveRequest {

    private String title;

    private Type type;

    private String thumbImgUrl;

    private String description;

    private LocalDateTime postDate;

    @Builder
    public ArticleSaveRequest(String title, Type type, String thumbImgUrl, String description, LocalDateTime postDate) {
        this.title = title;
        this.type = type;
        this.thumbImgUrl = thumbImgUrl;
        this.description = description;
        this.postDate = postDate;
    }

    public Article toEntity() {
        return Article.builder()
                .title(this.title)
                .type(this.type)
                .thumbImgUrl(this.thumbImgUrl)
                .description(this.description)
                .postDate(this.postDate)
                .build();
    }
}
