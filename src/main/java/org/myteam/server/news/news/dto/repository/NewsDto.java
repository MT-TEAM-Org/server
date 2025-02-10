package org.myteam.server.news.news.dto.repository;

import java.time.LocalDateTime;

import org.myteam.server.news.news.domain.NewsCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsDto {
	private Long id;
	private NewsCategory category;
	private String title;
	private String thumbImg;
	private LocalDateTime postDate;

	public NewsDto(Long id, NewsCategory category, String title, String thumbImg, LocalDateTime postDate) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
		this.postDate = postDate;
	}
}
