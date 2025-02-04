package org.myteam.server.news.newsCount.service;

import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.newsCount.dto.service.response.NewsRecommendResponse;
import org.myteam.server.news.newsCountMember.dto.service.request.NewsCountMemberSaveServiceRequest;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberReadService;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsCountService {

	private final NewsCountReadService newsCountReadService;
	private final SecurityReadService securityReadService;
	private final NewsCountMemberReadService newsCountMemberReadService;
	private final NewsCountMemberService newsCountMemberService;

	public NewsRecommendResponse recommendNews(Long newsId) {
		Long memberId = securityReadService.getMember().getId();
		newsCountMemberReadService.confirmExistMember(newsId, memberId);

		newsCountMemberService.save(NewsCountMemberSaveServiceRequest.createRequest(newsId, memberId));

		return NewsRecommendResponse.createResponse(newsCountReadService.findByNewsId(newsId).addRecommendCount());
	}

}
