package org.myteam.server.news.newsReply.dto.service.request;

import org.myteam.server.global.page.request.PageInfoServiceRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyServiceRequest extends PageInfoServiceRequest {

	private Long newsCommentId;

	@Builder
	public NewsReplyServiceRequest(Long newsCommentId, int size, int page) {
		super(page, size);
		this.newsCommentId = newsCommentId;
	}
}
