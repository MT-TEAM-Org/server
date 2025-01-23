package org.myteam.server.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.Base;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Article extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Type type;

    private String title;

    private String thumbImgUrl;

    private String description;

    private LocalDateTime postDate;

    @Builder
    public Article(Long id, Type type, String title, String thumbImgUrl, String description, LocalDateTime postDate) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.thumbImgUrl = thumbImgUrl;
        this.description = description;
        this.postDate = postDate;
    }
}
