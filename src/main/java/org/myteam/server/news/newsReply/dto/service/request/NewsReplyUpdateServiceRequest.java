package org.myteam.server.news.newsReply.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyUpdateServiceRequest {

	private Long newsReplyId;
	private String comment;
	private String imgUrl;

	@Builder
	public NewsReplyUpdateServiceRequest(Long newsReplyId, String comment, String imgUrl) {
		this.newsReplyId = newsReplyId;
		this.comment = comment;
		this.imgUrl = imgUrl;
	}
}
