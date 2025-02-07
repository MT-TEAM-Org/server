package org.myteam.server.news.newsComment.dto.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NewsCommentMemberResponse {

	private UUID memberPublicId;
	private String nickName;

	@Builder
	public NewsCommentMemberResponse(UUID memberPublicId, String nickName) {
		this.memberPublicId = memberPublicId;
		this.nickName = nickName;
	}
}
