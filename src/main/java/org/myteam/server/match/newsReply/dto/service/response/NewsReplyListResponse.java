package org.myteam.server.match.newsReply.dto.service.response;

import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.match.newsReply.dto.repository.NewsReplyDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyListResponse {

	private PageCustomResponse<NewsReplyDto> list;

	@Builder
	public NewsReplyListResponse(PageCustomResponse<NewsReplyDto> list) {
		this.list = list;
	}

	public static NewsReplyListResponse createResponse(PageCustomResponse<NewsReplyDto> list) {
		return NewsReplyListResponse.builder()
			.list(list)
			.build();
	}
}
