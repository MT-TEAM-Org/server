package org.myteam.server.news.newsComment.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentUpdateServiceRequest {

	private Long newsCommentId;
	private String comment;

	@Builder
	public NewsCommentUpdateServiceRequest(Long newsCommentId, String comment) {
		this.newsCommentId = newsCommentId;
		this.comment = comment;
	}
}
