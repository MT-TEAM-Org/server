package org.myteam.server.news.domain;

import org.myteam.server.global.domain.Base;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	private NewsCategory category;

	private String title;

	private String thumbImg;

	@Builder
	public News(Long id, NewsCategory category, String title, String thumbImg) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
	}
}
