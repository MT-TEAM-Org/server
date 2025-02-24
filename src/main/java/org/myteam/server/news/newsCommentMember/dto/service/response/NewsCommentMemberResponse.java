package org.myteam.server.news.newsCommentMember.dto.service.response;

import java.util.UUID;

import org.myteam.server.news.newsCommentMember.domain.NewsCommentMember;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentMemberResponse {

	private Long newsCommentMemberId;
	private Long newsCommentId;
	private UUID memberId;

	@Builder
	public NewsCommentMemberResponse(Long newsCommentMemberId, Long newsCommentId, UUID memberId) {
		this.newsCommentMemberId = newsCommentMemberId;
		this.newsCommentId = newsCommentId;
		this.memberId = memberId;
	}

	public static NewsCommentMemberResponse createResponse(NewsCommentMember newsCommentMember) {
		return NewsCommentMemberResponse.builder()
			.newsCommentMemberId(newsCommentMember.getId())
			.newsCommentId(newsCommentMember.getNewsComment().getId())
			.memberId(newsCommentMember.getMember().getPublicId())
			.build();
	}
}
