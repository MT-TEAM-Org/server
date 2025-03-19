package org.myteam.server.news.newsCount.service;

import java.util.UUID;

import org.myteam.server.comment.service.CommentCountService;
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
public class NewsCountService implements CommentCountService {

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

	@Override
	public void addCommentCount(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).addCommentCount();
	}

	@Override
	public void minusCommentCount(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).minusCommentCount();
	}

	@Override
	public void minusCommentCount(Long newsId, int minusCount) {
		newsCountReadService.findByNewsIdLock(newsId).minusCommentCount(minusCount);
	}

	public void addViewCount(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).addViewCount();
	}

	public void minusViewCont(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).minusViewCount();
	}

}
