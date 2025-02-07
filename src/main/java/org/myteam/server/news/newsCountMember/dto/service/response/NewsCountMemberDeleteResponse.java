package org.myteam.server.news.newsCountMember.dto.service.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCountMemberDeleteResponse {

	private Long newsId;
	private UUID memberId;

	@Builder
	public NewsCountMemberDeleteResponse(Long newsId, UUID memberId) {
		this.newsId = newsId;
		this.memberId = memberId;
	}

	public static NewsCountMemberDeleteResponse createResponse(Long newsId, UUID memberId) {
		return NewsCountMemberDeleteResponse.builder()
			.newsId(newsId)
			.memberId(memberId)
			.build();
	}
}
