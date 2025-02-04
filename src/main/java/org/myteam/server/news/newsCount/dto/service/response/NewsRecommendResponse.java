package org.myteam.server.news.newsCount.dto.service.response;

import org.myteam.server.news.newsCount.domain.NewsCount;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsRecommendResponse {

	private Long newsId;
	private int recommendCount;

	@Builder
	public NewsRecommendResponse(Long newsId, int recommendCount) {
		this.newsId = newsId;
		this.recommendCount = recommendCount;
	}

	public static NewsRecommendResponse createResponse(NewsCount newsCount) {
		return NewsRecommendResponse.builder()
			.newsId(newsCount.getNews().getId())
			.recommendCount(newsCount.getRecommendCount())
			.build();
	}
}
