package org.myteam.server.match.matchComment.dto.service.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchCommentMemberResponse {

	private UUID memberPublicId;
	private String nickName;

	@Builder
	public MatchCommentMemberResponse(UUID memberPublicId, String nickName) {
		this.memberPublicId = memberPublicId;
		this.nickName = nickName;
	}
}
