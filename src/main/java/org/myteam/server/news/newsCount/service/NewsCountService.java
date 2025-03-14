package org.myteam.server.news.newsCount.service;

import java.util.UUID;

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
		UUID memberId = securityReadService.getMember().getPublicId();
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
		newsCountReadService.findByNewsIdLock(newsId).addRecommendCount();
	}

	public void minusRecommendCount(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).minusRecommendCount();
	}

	public void addCommendCount(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).addCommentCount();
	}

	public void minusCommendCount(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).minusCommentCount();
	}

	public void minusCommendCount(Long newsId, int count) {
		newsCountReadService.findByNewsIdLock(newsId).minusCommentCount(count);
	}

	public void addViewCount(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).addViewCount();
	}

	public void minusViewCont(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).minusViewCount();
	}

}
