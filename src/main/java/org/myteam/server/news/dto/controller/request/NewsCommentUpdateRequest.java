package org.myteam.server.news.dto.controller.request;

import org.myteam.server.news.dto.service.request.NewsCommentUpdateServiceRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentUpdateRequest {

	@NotNull(message = "뉴스 댓글 ID는 필수입니다.")
	private Long newsCommentId;
	@NotNull(message = "뉴스 댓글 내용은 필수입니다.")
	private String comment;

	@Builder
	public NewsCommentUpdateRequest(Long newsCommentId, String comment) {
		this.newsCommentId = newsCommentId;
		this.comment = comment;
	}

	public NewsCommentUpdateServiceRequest toServiceRequest() {
		return NewsCommentUpdateServiceRequest.builder()
			.newsCommentId(newsCommentId)
			.comment(comment)
			.build();
	}
}
