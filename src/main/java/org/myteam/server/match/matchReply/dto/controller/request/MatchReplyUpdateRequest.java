package org.myteam.server.match.matchReply.dto.controller.request;

import org.myteam.server.match.matchReply.dto.service.request.MatchReplyUpdateServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplyUpdateRequest {

	@Schema(description = "경기 대댓글 ID")
	@NotNull(message = "경기 대댓글 ID는 필수입니다.")
	private Long matchReplyId;
	@Schema(description = "경기 대댓글 내용 ID")
	@NotNull(message = "경기 대댓글 내용은 필수입니다.")
	private String comment;

	@Builder
	public MatchReplyUpdateRequest(Long matchReplyId, String comment) {
		this.matchReplyId = matchReplyId;
		this.comment = comment;
	}

	public MatchReplyUpdateServiceRequest toServiceRequest() {
		return MatchReplyUpdateServiceRequest.builder()
			.matchReplyId(matchReplyId)
			.comment(comment)
			.build();
	}
}
