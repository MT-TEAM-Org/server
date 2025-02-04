package org.myteam.server.news.news.domain;

import java.time.LocalDateTime;

import org.myteam.server.global.domain.Base;
import org.myteam.server.news.newsCount.domain.NewsCount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_news")
public class News extends Base {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private NewsCategory category;

	private String title;

	private String thumbImg;

	private LocalDateTime postTime;

	@Builder
	public News(Long id, NewsCategory category, String title, String thumbImg, LocalDateTime postTime) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
		this.postTime = postTime;
	}
}
