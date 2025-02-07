package org.myteam.server.news.newsComment.dto.controller.request;

import org.myteam.server.news.newsComment.dto.service.request.NewsCommentSaveServiceRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentSaveRequest {

	@NotNull(message = "뉴스ID는 필수입니다.")
	private Long newsId;
	@NotNull(message = "뉴스 댓글은 필수입니다.")
	private String comment;

	@Builder
	public NewsCommentSaveRequest(Long newsId, String comment) {
		this.newsId = newsId;
		this.comment = comment;
	}

	public NewsCommentSaveServiceRequest toServiceRequest(String ip) {
		return NewsCommentSaveServiceRequest.builder()
			.newsId(newsId)
			.comment(comment)
			.ip(ip)
			.build();
	}
}
