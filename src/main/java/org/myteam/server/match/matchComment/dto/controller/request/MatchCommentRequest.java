package org.myteam.server.match.matchComment.dto.controller.request;

import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.match.matchComment.dto.service.request.MatchCommentServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatchCommentRequest extends PageInfoRequest {

	@Schema(description = "경기 ID")
	@NotNull(message = "경기 ID는 필수입니다.")
	private Long matchId;

	@Builder
	public MatchCommentRequest(Long matchId, int page, int size) {
		super(page, size);
		this.matchId = matchId;
	}

	public MatchCommentServiceRequest toServiceRequest() {
		return MatchCommentServiceRequest.builder()
			.matchId(matchId)
			.size(getSize())
			.page(getPage())
			.build();
	}
}
