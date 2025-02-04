package org.myteam.server.news.newsCountMember.dto.service.response;

import org.myteam.server.news.newsCountMember.domain.NewsCountMember;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCountMemberResponse {

	private Long newsCountMemberId;
	private Long newsId;
	private Long memberId;

	@Builder
	public NewsCountMemberResponse(Long newsCountMemberId, Long newsId, Long memberId) {
		this.newsCountMemberId = newsCountMemberId;
		this.newsId = newsId;
		this.memberId = memberId;
	}

	public static NewsCountMemberResponse createResponse(NewsCountMember newsCountMember) {
		return NewsCountMemberResponse.builder()
			.newsCountMemberId(newsCountMember.getId())
			.newsId(newsCountMember.getNews().getId())
			.memberId(newsCountMember.getMember().getId())
			.build();
	}
}
