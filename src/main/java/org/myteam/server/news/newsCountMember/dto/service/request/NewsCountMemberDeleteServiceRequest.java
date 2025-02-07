package org.myteam.server.news.newsCountMember.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCountMemberDeleteServiceRequest {

	private Long newsId;
	private Long memberId;

	@Builder
	public NewsCountMemberDeleteServiceRequest(Long newsId, Long memberId) {
		this.newsId = newsId;
		this.memberId = memberId;
	}

	public static NewsCountMemberDeleteServiceRequest createRequest(Long newsId, Long memberId) {
		return NewsCountMemberDeleteServiceRequest.builder()
			.newsId(newsId)
			.memberId(memberId)
			.build();
	}
}
