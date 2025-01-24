package org.myteam.server.news.dto.controller.response;

import org.myteam.server.news.dto.service.response.NewsDto;
import org.myteam.server.global.page.response.PageCustom;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsListResponse {

	private PageCustom<NewsDto> newsList;

	@Builder
	public NewsListResponse(PageCustom<NewsDto> newsList) {
		this.newsList = newsList;
	}

	public static NewsListResponse of(PageCustom<NewsDto> newsList) {
		return NewsListResponse.builder()
			.newsList(newsList)
			.build();
	}
}
