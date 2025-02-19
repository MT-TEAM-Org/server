package org.myteam.server.news.newsReplyMember.dto.service.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyMemberDeleteResponse {

	private Long newsReplyId;
	private UUID memberId;

	@Builder
	public NewsReplyMemberDeleteResponse(Long newsReplyId, UUID memberId) {
		this.newsReplyId = newsReplyId;
		this.memberId = memberId;
	}

	public static NewsReplyMemberDeleteResponse createResponse(Long newsReplyId, UUID memberId) {
		return NewsReplyMemberDeleteResponse.builder()
			.newsReplyId(newsReplyId)
			.memberId(memberId)
			.build();
	}
}
