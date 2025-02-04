package org.myteam.server.news.news.dto.service.response;

import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.dto.repository.NewsDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsResponse {
	private Long id;

	private NewsCategory category;

	private String title;

	private String thumbImg;

	@Builder
	public NewsResponse(Long id, NewsCategory category, String title, String thumbImg) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
	}

	public static NewsResponse of(NewsDto newsDto) {
		return NewsResponse.builder()
			.id(newsDto.getId())
			.category(newsDto.getCategory())
			.title(newsDto.getTitle())
			.thumbImg(newsDto.getThumbImg())
			.build();
	}
}
