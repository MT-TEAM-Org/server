package org.myteam.server.news.newsCountMember.dto.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCountMemberDeleteResponse {

	private Long newsId;
	private Long memberId;

	@Builder
	public NewsCountMemberDeleteResponse(Long newsId, Long memberId) {
		this.newsId = newsId;
		this.memberId = memberId;
	}

	public static NewsCountMemberDeleteResponse createResponse(Long newsId, Long memberId) {
		return NewsCountMemberDeleteResponse.builder()
			.newsId(newsId)
			.memberId(memberId)
			.build();
	}
}
