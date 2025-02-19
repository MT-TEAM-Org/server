package org.myteam.server.news.newsReplyMember.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentMemberDeleteResponse {

	private Long newsCommentId;
	private UUID memberId;

	@Builder
	public NewsCommentMemberDeleteResponse(Long newsCommentId, UUID memberId) {
		this.newsCommentId = newsCommentId;
		this.memberId = memberId;
	}

	public static NewsCommentMemberDeleteResponse createResponse(Long newsCommentId, UUID memberId) {
		return NewsCommentMemberDeleteResponse.builder()
			.newsCommentId(newsCommentId)
			.memberId(memberId)
			.build();
	}
}
