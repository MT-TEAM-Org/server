package org.myteam.server.news.newsComment.service;

import java.util.List;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.dto.repository.NewsCommentDto;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentServiceRequest;
import org.myteam.server.news.newsComment.dto.service.response.NewsCommentListResponse;
import org.myteam.server.news.newsComment.repository.NewsCommentLockRepository;
import org.myteam.server.news.newsComment.repository.NewsCommentQueryRepository;
import org.myteam.server.news.newsComment.repository.NewsCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsCommentReadService {

	private final NewsCommentRepository newsCommentRepository;
	private final NewsCommentLockRepository newsCommentLockRepository;
	private final NewsCommentQueryRepository newsCommentQueryRepository;
	private final SecurityReadService securityReadService;

	public NewsCommentListResponse findByNewsId(NewsCommentServiceRequest newsCommentServiceRequest) {
		Page<NewsCommentDto> list = newsCommentQueryRepository.getNewsCommentList(
			newsCommentServiceRequest.getNewsId(),
			securityReadService.getAuthenticatedPublicId(),
			newsCommentServiceRequest.toPageable()
		);

		return NewsCommentListResponse.createResponse(PageCustomResponse.of(list));
	}

	public List<NewsCommentDto> findBestByNewsId(Long newsId) {
		return newsCommentQueryRepository.getNewsBestCommentList(newsId, securityReadService.getAuthenticatedPublicId());
	}

	public NewsComment findById(Long newsCommentId) {
		return newsCommentRepository.findById(newsCommentId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_COMMENT_NOT_FOUND));
	}

	public NewsComment findByIdLock(Long newsCommentId) {
		return newsCommentLockRepository.findById(newsCommentId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_COMMENT_NOT_FOUND));
	}

	public boolean existsById(Long id) {
		return newsCommentRepository.existsById(id);
	}

}
