package org.myteam.server.match.matchReply.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplyUpdateServiceRequest {

	private Long matchReplyId;
	private String comment;
	private String imgUrl;

	@Builder
	public MatchReplyUpdateServiceRequest(Long matchReplyId, String comment, String imgUrl) {
		this.matchReplyId = matchReplyId;
		this.comment = comment;
		this.imgUrl = imgUrl;
	}
}
