package org.myteam.server.news.news.dto.repository;

import java.time.LocalDateTime;

import org.myteam.server.news.news.domain.NewsCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsDto {

	@Schema(description = "뉴스 ID")
	private Long id;
	@Schema(description = "뉴스 카테고리")
	private NewsCategory category;
	@Schema(description = "뉴스 제목")
	private String title;
	@Schema(description = "뉴스 썸네일 이미지")
	private String thumbImg;
	@Schema(description = "뉴스 계시 날짜")
	private LocalDateTime postDate;

	public NewsDto(Long id, NewsCategory category, String title, String thumbImg, LocalDateTime postDate) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
		this.postDate = postDate;
	}
}
