package org.myteam.server.news.dto.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentMemberResponse {

	private Long memberId;
	private String nickName;

	@Builder
	public NewsCommentMemberResponse(Long memberId, String nickName) {
		this.memberId = memberId;
		this.nickName = nickName;
	}
}
