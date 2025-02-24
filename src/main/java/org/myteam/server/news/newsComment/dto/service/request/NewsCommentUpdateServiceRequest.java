package org.myteam.server.news.newsComment.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentUpdateServiceRequest {

	private Long newsCommentId;
	private String comment;
	private String imgUrl;

	@Builder
	public NewsCommentUpdateServiceRequest(Long newsCommentId, String comment, String imgUrl) {
		this.newsCommentId = newsCommentId;
		this.comment = comment;
		this.imgUrl = imgUrl;
	}
}
