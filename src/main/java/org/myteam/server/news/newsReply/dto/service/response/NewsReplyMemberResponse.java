package org.myteam.server.news.newsReply.dto.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NewsReplyMemberResponse {

	private UUID memberPublicId;
	private String nickName;

	@Builder
	public NewsReplyMemberResponse(UUID memberPublicId, String nickName) {
		this.memberPublicId = memberPublicId;
		this.nickName = nickName;
	}
}
