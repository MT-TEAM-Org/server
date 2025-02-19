package org.myteam.server.match.newsReply.dto.controller.request;

import org.myteam.server.match.newsReply.dto.service.request.NewsReplySaveServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplySaveRequest {

	@Schema(description = "뉴스 댓글 ID")
	@NotNull(message = "뉴스 댓글 ID는 필수입니다.")
	private Long newsCommentId;
	@Schema(description = "뉴스 대댓글 내용")
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
