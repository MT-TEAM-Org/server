package org.myteam.server.match.matchComment.dto.service.request;

import org.myteam.server.global.page.request.PageInfoServiceRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentServiceRequest extends PageInfoServiceRequest {

	private Long matchId;

	@Builder
	public MatchCommentServiceRequest(Long matchId, int size, int page) {
		super(page, size);
		this.matchId = matchId;
	}
}
