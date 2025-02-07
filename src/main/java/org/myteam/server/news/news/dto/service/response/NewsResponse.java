package org.myteam.server.news.news.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.newsCount.domain.NewsCount;

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

	private int recommendCount;

	private int commentCount;

	private int viewCount;

	private LocalDateTime postTime;

	@Builder
	public NewsResponse(Long id, NewsCategory category, String title, String thumbImg, int recommendCount,
		int commentCount,
		int viewCount, LocalDateTime postTime) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
		this.recommendCount = recommendCount;
		this.commentCount = commentCount;
		this.viewCount = viewCount;
		this.postTime = postTime;
	}


	public static NewsResponse createResponse(NewsDto newsDto) {
		return NewsResponse.builder()
			.id(newsDto.getId())
			.category(newsDto.getCategory())
			.title(newsDto.getTitle())
			.thumbImg(newsDto.getThumbImg())
			.build();
	}

	public static NewsResponse createResponse(News news, NewsCount newsCount) {
		return NewsResponse.builder()
			.id(news.getId())
			.category(news.getCategory())
			.title(news.getTitle())
			.thumbImg(news.getThumbImg())
			.postTime(news.getPostTime())
			.recommendCount(newsCount.getRecommendCount())
			.commentCount(newsCount.getCommentCount())
			.viewCount(newsCount.getViewCount())
			.build();
	}
}
