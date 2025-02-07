package org.myteam.server.news.newsCountMember.dto.service.request;

import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCountMemberSaveServiceRequest {

	private Long newsId;
	private Long memberId;

	@Builder
	public NewsCountMemberSaveServiceRequest(Long newsId, Long memberId) {
		this.newsId = newsId;
		this.memberId = memberId;
	}

	public NewsCountMember toEntity(News news, Member member) {
		return NewsCountMember.builder()
			.news(news)
			.member(member)
			.build();
	}

	public static NewsCountMemberSaveServiceRequest createRequest(Long newsId, Long memberId) {
		return NewsCountMemberSaveServiceRequest.builder()
			.newsId(newsId)
			.memberId(memberId)
			.build();
	}
}
