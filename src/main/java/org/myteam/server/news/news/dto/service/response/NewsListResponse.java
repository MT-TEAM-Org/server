package org.myteam.server.news.news.dto.service.response;

import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.news.news.dto.repository.NewsDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsListResponse {

	private PageCustomResponse<NewsDto> list;

	@Builder
	public NewsListResponse(PageCustomResponse<NewsDto> list) {
		this.list = list;
	}

	public static NewsListResponse createResponse(PageCustomResponse<NewsDto> list) {
		return NewsListResponse.builder()
			.list(list)
			.build();
	}
}
