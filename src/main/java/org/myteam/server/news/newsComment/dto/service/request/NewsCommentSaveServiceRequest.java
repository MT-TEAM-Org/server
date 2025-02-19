package org.myteam.server.news.newsComment.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentSaveServiceRequest {
	private Long newsId;
	private String comment;
	private String ip;
	private String imgUrl;

	@Builder
	public NewsCommentSaveServiceRequest(Long newsId, String comment, String ip, String imgUrl) {
		this.newsId = newsId;
		this.comment = comment;
		this.ip = ip;
		this.imgUrl = imgUrl;
	}
}
