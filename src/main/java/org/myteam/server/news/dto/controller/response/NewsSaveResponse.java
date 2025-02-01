package org.myteam.server.news.dto.controller.response;

import java.time.LocalDateTime;

import org.myteam.server.news.domain.News;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsSaveResponse {
	private Long id;

	private String title;

	private String thumbImg;

	@Builder
	public NewsSaveResponse(Long id, String title, String thumbImg, String description, LocalDateTime postDate) {
		this.id = id;
		this.title = title;
		this.thumbImg = thumbImg;
	}

	public static NewsSaveResponse of(News news) {
		return NewsSaveResponse.builder()
			.id(news.getId())
			.title(news.getTitle())
			.thumbImg(news.getThumbImg())
			.build();
	}
}
