package org.myteam.server.news.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyUpdateServiceRequest {

	private Long newsReplyId;
	private String comment;

	@Builder
	public NewsReplyUpdateServiceRequest(Long newsReplyId, String comment) {
		this.newsReplyId = newsReplyId;
		this.comment = comment;
	}
}
