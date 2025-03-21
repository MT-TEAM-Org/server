package org.myteam.server.news.newsCount.service;

import java.util.UUID;

import org.myteam.server.comment.service.CommentCountService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.newsCount.dto.service.response.NewsRecommendResponse;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberReadService;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberService;
import org.myteam.server.viewCountMember.domain.ViewType;
import org.myteam.server.viewCountMember.service.ViewCountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsCountService implements CommentCountService {

	private final NewsCountReadService newsCountReadService;
	private final SecurityReadService securityReadService;
	private final NewsCountMemberReadService newsCountMemberReadService;
	private final NewsCountMemberService newsCountMemberService;
	private final ViewCountService viewCountService;

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

	public void addViewCount(HttpServletRequest request, HttpServletResponse response, Long newsId) {
		if (!viewCountService.confirmPostView(request, response, ViewType.NEWS, newsId,
			securityReadService.getAuthenticatedPublicId())) {
			newsCountReadService.findByNewsIdLock(newsId).addViewCount();
		}
	}

	public void minusViewCont(Long newsId) {
		newsCountReadService.findByNewsIdLock(newsId).minusViewCount();
	}

}
