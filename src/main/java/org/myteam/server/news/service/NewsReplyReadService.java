package org.myteam.server.news.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.news.domain.NewsReply;
import org.myteam.server.news.dto.repository.NewsReplyDto;
import org.myteam.server.news.dto.service.request.NewsReplyServiceRequest;
import org.myteam.server.news.dto.service.response.NewsReplyListResponse;
import org.myteam.server.news.repository.NewsReplyQueryRepository;
import org.myteam.server.news.repository.NewsReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsReplyReadService {

	private final NewsReplyRepository newsReplyRepository;
	private final NewsReplyQueryRepository newsReplyQueryRepository;

	public NewsReplyListResponse findByNewsCommentId(NewsReplyServiceRequest newsReplyServiceRequest) {
		Page<NewsReplyDto> list = newsReplyQueryRepository.getNewsReplyList(
			newsReplyServiceRequest.getNewsCommentId(),
			newsReplyServiceRequest.toPageable()
		);

		return NewsReplyListResponse.createResponse(PageCustomResponse.of(list));
	}

	public NewsReply findById(Long newsReplyId) {
		return newsReplyRepository.findById(newsReplyId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_REPLY_NOT_FOUND));
	}

}
