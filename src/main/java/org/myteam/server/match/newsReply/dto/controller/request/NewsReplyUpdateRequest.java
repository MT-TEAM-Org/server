package org.myteam.server.match.newsReply.dto.controller.request;

import org.myteam.server.match.newsReply.dto.service.request.NewsReplyUpdateServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyUpdateRequest {

	@Schema(description = "뉴스 대댓글 ID")
	@NotNull(message = "뉴스 대댓글 ID는 필수입니다.")
	private Long newsReplyId;
	@Schema(description = "뉴스 대댓글 내용 ID")
	@NotNull(message = "뉴스 대댓글 내용은 필수입니다.")
	private String comment;

	@Builder
	public NewsReplyUpdateRequest(Long newsReplyId, String comment) {
		this.newsReplyId = newsReplyId;
		this.comment = comment;
	}

	public NewsReplyUpdateServiceRequest toServiceRequest() {
		return NewsReplyUpdateServiceRequest.builder()
			.newsReplyId(newsReplyId)
			.comment(comment)
			.build();
	}
}
