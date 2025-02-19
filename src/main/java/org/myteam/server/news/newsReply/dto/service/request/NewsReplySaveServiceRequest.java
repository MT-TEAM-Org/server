package org.myteam.server.news.newsReply.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplySaveServiceRequest {
	private Long newsCommentId;
	private String comment;
	private String ip;
	private String imgUrl;

	@Builder
	public NewsReplySaveServiceRequest(Long newsCommentId, String comment, String ip, String imgUrl) {
		this.newsCommentId = newsCommentId;
		this.comment = comment;
		this.ip = ip;
		this.imgUrl = imgUrl;
	}
}
