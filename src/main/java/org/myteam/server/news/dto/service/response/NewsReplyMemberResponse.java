package org.myteam.server.news.dto.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyMemberResponse {

	private Long memberId;
	private String nickName;

	@Builder
	public NewsReplyMemberResponse(Long memberId, String nickName) {
		this.memberId = memberId;
		this.nickName = nickName;
	}
}
