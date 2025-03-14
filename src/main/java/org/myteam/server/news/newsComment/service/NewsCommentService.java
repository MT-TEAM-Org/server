package org.myteam.server.news.newsComment.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentSaveServiceRequest;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentUpdateServiceRequest;
import org.myteam.server.news.newsComment.dto.service.response.NewsCommentResponse;
import org.myteam.server.news.newsComment.repository.NewsCommentRepository;
import org.myteam.server.news.newsCommentMember.service.NewsCommentMemberReadService;
import org.myteam.server.news.newsCommentMember.service.NewsCommentMemberService;
import org.myteam.server.news.newsCount.service.NewsCountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsCommentService {

	private final NewsCommentRepository newsCommentRepository;
	private final NewsCommentReadService newsCommentReadService;
	private final NewsReadService newsReadService;
	private final SecurityReadService securityReadService;
	private final NewsCountService newsCountService;
	private final NewsCommentMemberReadService newsCommentMemberReadService;
	private final NewsCommentMemberService newsCommentMemberService;

	public NewsCommentResponse save(NewsCommentSaveServiceRequest newsCommentSaveServiceRequest) {
		News news = newsReadService.findById(newsCommentSaveServiceRequest.getNewsId());
		Member member = securityReadService.getMember();

		NewsComment newsComment = newsCommentRepository.save(
			NewsComment.createEntity(news, member, newsCommentSaveServiceRequest.getComment(),
				newsCommentSaveServiceRequest.getIp(), newsCommentSaveServiceRequest.getImgUrl()));

		newsCountService.addCommentCount(news.getId());

		return NewsCommentResponse.createResponse(newsComment, member);
	}

	public Long update(NewsCommentUpdateServiceRequest newsCommentUpdateServiceRequest) {
		Member member = securityReadService.getMember();
		NewsComment newsComment = newsCommentReadService.findById(newsCommentUpdateServiceRequest.getNewsCommentId());

		newsComment.confirmMember(member);
		newsComment.update(newsCommentUpdateServiceRequest.getComment(), newsCommentUpdateServiceRequest.getImgUrl());

		return newsComment.getId();
	}

	public Long delete(Long newsCommentId) {
		Member member = securityReadService.getMember();
		NewsComment newsComment = newsCommentReadService.findById(newsCommentId);

		newsComment.confirmMember(member);

		newsCommentRepository.deleteById(newsCommentId);

		newsCountService.minusCommentCount(newsComment.getNews().getId());

		return newsComment.getId();
	}

	public Long recommend(Long newsCommentId) {
		Member member = securityReadService.getMember();
		NewsComment newsComment = newsCommentReadService.findByIdLock(newsCommentId);

		newsCommentMemberReadService.confirmExistMember(newsCommentId, member.getPublicId());

		newsCommentMemberService.save(newsCommentId);

		newsComment.addRecommendCount();

		return newsComment.getId();
	}

	public Long cancelRecommend(Long newsCommentId) {
		Member member = securityReadService.getMember();
		NewsComment newsComment = newsCommentReadService.findByIdLock(newsCommentId);

		if (!newsCommentMemberReadService.confirmRecommendMember(newsCommentId, member.getPublicId())) {
			throw new PlayHiveException(ErrorCode.NEWS_COMMENT_RECOMMEND_NOT_FOUND);
		}

		newsCommentMemberService.deleteByNewsCommentIdMemberId(newsCommentId);

		newsComment.minusRecommendCount();

		return newsComment.getId();
	}
}
