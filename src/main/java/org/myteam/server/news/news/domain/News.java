package org.myteam.server.news.news.domain;

import java.time.LocalDateTime;

import org.myteam.server.global.domain.Base;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.Category;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_news")
public class News extends Base {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private Category category;

	private String title;

	private String thumbImg;

	private String source;
	@Lob
	private String content;

	private LocalDateTime postDate;

	@Builder
	public News(Long id, Category category, String title, String thumbImg, LocalDateTime postDate, String source, String content) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
		this.postDate = postDate;
		this.source = source;
		this.content = content;
	}
}
