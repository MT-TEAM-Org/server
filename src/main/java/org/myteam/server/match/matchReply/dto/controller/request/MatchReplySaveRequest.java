package org.myteam.server.match.matchReply.dto.controller.request;

import org.myteam.server.match.matchReply.dto.service.request.MatchReplySaveServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplySaveRequest {

	@Schema(description = "경기 댓글 ID")
	@NotNull(message = "경기 댓글 ID는 필수입니다.")
	private Long matchCommentId;
	@Schema(description = "경기 대댓글 내용")
	@NotNull(message = "경기 대댓글은 필수입니다.")
	private String comment;

	@Builder
	public MatchReplySaveRequest(Long matchCommentId, String comment) {
		this.matchCommentId = matchCommentId;
		this.comment = comment;
	}

	public MatchReplySaveServiceRequest toServiceRequest(String ip) {
		return MatchReplySaveServiceRequest.builder()
			.matchCommentId(matchCommentId)
			.comment(comment)
			.ip(ip)
			.build();
	}
}
