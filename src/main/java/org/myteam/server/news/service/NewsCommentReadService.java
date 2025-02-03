package org.myteam.server.news.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.news.domain.NewsComment;
import org.myteam.server.news.dto.repository.NewsCommentDto;
import org.myteam.server.news.dto.service.request.NewsCommentServiceRequest;
import org.myteam.server.news.dto.service.response.NewsCommentListResponse;
import org.myteam.server.news.repository.NewsCommentQueryRepository;
import org.myteam.server.news.repository.NewsCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsCommentReadService {

	private final NewsCommentRepository newsCommentRepository;
	private final NewsCommentQueryRepository newsCommentQueryRepository;

	public NewsCommentListResponse findByNewsId(NewsCommentServiceRequest newsCommentServiceRequest) {

		Page<NewsCommentDto> list = newsCommentQueryRepository.getNewsCommentList(
			newsCommentServiceRequest.getNewsId(),
			newsCommentServiceRequest.toPageable()
		);

		return NewsCommentListResponse.createResponse(PageCustomResponse.of(list));
	}

	public NewsComment findById(Long newsCommentId) {
		return newsCommentRepository.findById(newsCommentId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_COMMENT_NOT_FOUND));
	}

}
