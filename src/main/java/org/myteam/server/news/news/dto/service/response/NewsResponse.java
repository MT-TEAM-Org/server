package org.myteam.server.news.news.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.global.domain.Category;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCount.domain.NewsCount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsResponse {

	@Schema(description = "뉴스 ID")
	private Long id;
	@Schema(description = "뉴스 카테고리")
	private Category category;
	@Schema(description = "뉴스 제목")
	private String title;
	@Schema(description = "뉴스 썸네일 이미지")
	private String thumbImg;
	@Schema(description = "뉴스 추천수")
	private int recommendCount;
	@Schema(description = "뉴스 댓글수")
	private int commentCount;
	@Schema(description = "뉴스 조회수")
	private int viewCount;
	@Schema(description = "뉴스 계시 날짜")
	private LocalDateTime postDate;
	@Schema(description = "개인 추천 여부")
	private boolean isRecommend;
	@Schema(description = "출처")
	private String source;
	@Schema(description = "본문")
	private String content;

	@Builder
	public NewsResponse(Long id, Category category, String title, String thumbImg, int recommendCount,
		int commentCount,
		int viewCount, LocalDateTime postDate, boolean isRecommend, String source, String content) {
		this.id = id;
		this.category = category;
		this.title = title;
		this.thumbImg = thumbImg;
		this.recommendCount = recommendCount;
		this.commentCount = commentCount;
		this.viewCount = viewCount;
		this.postDate = postDate;
		this.isRecommend = isRecommend;
		this.source = source;
		this.content = content;
	}

	public static NewsResponse createResponse(News news, NewsCount newsCount, boolean isRecommend) {
		return NewsResponse.builder()
			.id(news.getId())
			.category(news.getCategory())
			.title(news.getTitle())
			.thumbImg(news.getThumbImg())
			.postDate(news.getPostDate())
			.recommendCount(newsCount.getRecommendCount())
			.commentCount(newsCount.getCommentCount())
			.viewCount(newsCount.getViewCount())
			.isRecommend(isRecommend)
			.source(news.getSource())
			.content(news.getContent())
			.build();
	}
}
