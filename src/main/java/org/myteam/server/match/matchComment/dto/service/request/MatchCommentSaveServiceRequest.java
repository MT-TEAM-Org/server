package org.myteam.server.match.matchComment.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentSaveServiceRequest {
	private Long matchId;
	private String comment;
	private String ip;
	private String imgUrl;

	@Builder
	public MatchCommentSaveServiceRequest(Long matchId, String comment, String ip, String imgUrl) {
		this.matchId = matchId;
		this.comment = comment;
		this.ip = ip;
		this.imgUrl = imgUrl;
	}
}
