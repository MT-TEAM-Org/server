package org.myteam.server.news.dto.service.response;

import org.myteam.server.news.entity.NewsCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsDto {
	private Long id;
	private NewsCategory category;
	private String title;
	private String thumbImg;

	public NewsDto(Long id, NewsCategory category, String title, String thumbImg) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
	}
}
