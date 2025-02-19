package org.myteam.server.news.newsReplyMember.dto.service.response;

import java.util.UUID;

import org.myteam.server.news.newsReplyMember.domain.NewsReplyMember;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyMemberResponse {

	private Long newsReplyMemberId;
	private Long newsReplyId;
	private UUID memberId;

	@Builder
	public NewsReplyMemberResponse(Long newsReplyMemberId, Long newsReplyId, UUID memberId) {
		this.newsReplyMemberId = newsReplyMemberId;
		this.newsReplyId = newsReplyId;
		this.memberId = memberId;
	}

	public static NewsReplyMemberResponse createResponse(NewsReplyMember newsReplyMember) {
		return NewsReplyMemberResponse.builder()
			.newsReplyMemberId(newsReplyMember.getId())
			.newsReplyId(newsReplyMember.getNewsReply().getId())
			.memberId(newsReplyMember.getMember().getPublicId())
			.build();
	}
}
