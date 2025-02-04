package org.myteam.server.news.dto.controller.request;

import org.myteam.server.news.dto.service.request.NewsReplySaveServiceRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplySaveRequest {

	@NotNull(message = "뉴스 댓글 ID는 필수입니다.")
	private Long newsCommentId;
	@NotNull(message = "뉴스 대댓글은 필수입니다.")
	private String comment;

	@Builder
	public NewsReplySaveRequest(Long newsCommentId, String comment) {
		this.newsCommentId = newsCommentId;
		this.comment = comment;
	}

	public NewsReplySaveServiceRequest toServiceRequest(String ip) {
		return NewsReplySaveServiceRequest.builder()
			.newsCommentId(newsCommentId)
			.comment(comment)
			.ip(ip)
			.build();
	}
}
