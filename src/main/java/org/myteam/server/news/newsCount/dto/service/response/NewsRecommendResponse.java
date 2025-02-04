package org.myteam.server.news.newsCount.dto.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsRecommendResponse {

	private Long newsId;

	@Builder
	public NewsRecommendResponse(Long newsId) {
		this.newsId = newsId;
	}

	public static NewsRecommendResponse createResponse(Long newsId) {
		return NewsRecommendResponse.builder()
			.newsId(newsId)
			.build();
	}
}
