package org.myteam.server.news.newsComment.dto.service.response;

import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.news.newsComment.dto.repository.NewsCommentDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentListResponse {

	private PageCustomResponse<NewsCommentDto> list;

	@Builder
	public NewsCommentListResponse(PageCustomResponse<NewsCommentDto> list) {
		this.list = list;
	}

	public static NewsCommentListResponse createResponse(PageCustomResponse<NewsCommentDto> list) {
		return NewsCommentListResponse.builder()
			.list(list)
			.build();
	}
}
