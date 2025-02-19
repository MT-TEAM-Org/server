package org.myteam.server.match.matchReply.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchReplySaveServiceRequest {
	private Long matchCommentId;
	private String comment;
	private String ip;
	private String imgUrl;

	@Builder
	public MatchReplySaveServiceRequest(Long matchCommentId, String comment, String ip, String imgUrl) {
		this.matchCommentId = matchCommentId;
		this.comment = comment;
		this.ip = ip;
		this.imgUrl = imgUrl;
	}
}
