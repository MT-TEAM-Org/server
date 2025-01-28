package org.myteam.server.news.dto.controller.response;

import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.news.dto.service.response.NewsDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsListResponse {

	private PageCustomResponse<NewsDto> newsList;

	@Builder
	public NewsListResponse(PageCustomResponse<NewsDto> newsList) {
		this.newsList = newsList;
	}

	public static NewsListResponse of(PageCustomResponse<NewsDto> newsList) {
		return NewsListResponse.builder()
			.newsList(newsList)
			.build();
	}
}
