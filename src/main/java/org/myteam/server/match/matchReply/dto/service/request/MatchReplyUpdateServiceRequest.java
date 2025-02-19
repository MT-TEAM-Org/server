package org.myteam.server.match.matchReply.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplyUpdateServiceRequest {

	private Long matchReplyId;
	private String comment;

	@Builder
	public MatchReplyUpdateServiceRequest(Long matchReplyId, String comment) {
		this.matchReplyId = matchReplyId;
		this.comment = comment;
	}
}
