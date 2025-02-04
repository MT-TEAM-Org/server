package org.myteam.server.news.newsCount.service;

import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.newsCount.dto.service.response.NewsRecommendResponse;
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

		newsCountMemberService.save(newsId);

		addRecommendCount(newsId);

		return NewsRecommendResponse.createResponse(newsId);
	}

	public NewsRecommendResponse cancelRecommendNews(Long newsId) {
		newsCountMemberService.deleteByNewsIdMemberId(newsId);

		minusRecommendCount(newsId);

		return NewsRecommendResponse.createResponse(newsId);
	}

	public void addRecommendCount(Long newsId) {
		newsCountReadService.findByNewsId(newsId).addRecommendCount();
	}

	public void minusRecommendCount(Long newsId) {
		newsCountReadService.findByNewsId(newsId).minusRecommendCount();
	}

	public void addCommendCount(Long newsId) {
		newsCountReadService.findByNewsId(newsId).addCommentCount();
	}

	public void minusCommendCount(Long newsId) {
		newsCountReadService.findByNewsId(newsId).minusCommentCount();
	}

	public void addViewCount(Long newsId) {
		newsCountReadService.findByNewsId(newsId).addViewCount();
	}

	public void minusViewCont(Long newsId) {
		newsCountReadService.findByNewsId(newsId).minusViewCount();
	}

}
