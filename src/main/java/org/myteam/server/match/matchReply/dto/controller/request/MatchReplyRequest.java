package org.myteam.server.match.matchReply.dto.controller.request;

import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.match.matchReply.dto.service.request.MatchReplyServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatchReplyRequest extends PageInfoRequest {

	@Schema(description = "경기 댓글 ID")
	@NotNull(message = "경기 댓글 ID는 필수입니다.")
	private Long matchCommentId;

	@Builder
	public MatchReplyRequest(Long matchCommentId, int page, int size) {
		super(page, size);
		this.matchCommentId = matchCommentId;
	}

	public MatchReplyServiceRequest toServiceRequest() {
		return MatchReplyServiceRequest.builder()
			.matchCommentId(matchCommentId)
			.size(getSize())
			.page(getPage())
			.build();
	}
}
