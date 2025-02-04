package org.myteam.server.news.newsComment.service;

import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentSaveServiceRequest;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentUpdateServiceRequest;
import org.myteam.server.news.newsComment.dto.service.response.NewsCommentResponse;
import org.myteam.server.news.newsComment.repository.NewsCommentRepository;
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

	public NewsCommentResponse save(NewsCommentSaveServiceRequest newsCommentSaveServiceRequest) {
		News news = newsReadService.findById(newsCommentSaveServiceRequest.getNewsId());
		Member member = securityReadService.getMember();

		return NewsCommentResponse.createResponse(
			newsCommentRepository.save(
				NewsComment.createNewsComment(news, member, newsCommentSaveServiceRequest.getComment(),
					newsCommentSaveServiceRequest.getIp())), member
		);
	}

	public Long update(NewsCommentUpdateServiceRequest newsCommentUpdateServiceRequest) {
		Member member = securityReadService.getMember();
		NewsComment newsComment = newsCommentReadService.findById(newsCommentUpdateServiceRequest.getNewsCommentId());

		newsComment.confirmMember(member);
		newsComment.updateComment(newsCommentUpdateServiceRequest.getComment());

		return newsComment.getId();
	}

	public Long delete(Long newsCommentId) {
		Member member = securityReadService.getMember();
		NewsComment newsComment = newsCommentReadService.findById(newsCommentId);

		newsComment.confirmMember(member);

		newsCommentRepository.deleteById(newsCommentId);
		return newsComment.getId();
	}
}
