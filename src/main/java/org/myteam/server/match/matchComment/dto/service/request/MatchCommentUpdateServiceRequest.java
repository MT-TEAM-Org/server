package org.myteam.server.match.matchComment.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentUpdateServiceRequest {

	private Long matchCommentId;
	private String comment;

	@Builder
	public MatchCommentUpdateServiceRequest(Long matchCommentId, String comment) {
		this.matchCommentId = matchCommentId;
		this.comment = comment;
	}
}
