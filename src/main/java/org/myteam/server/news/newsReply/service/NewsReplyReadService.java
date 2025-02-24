package org.myteam.server.news.newsReply.service;

import java.util.UUID;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.newsComment.repository.NewsCommentLockRepository;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReply.dto.repository.NewsReplyDto;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplyServiceRequest;
import org.myteam.server.news.newsReply.dto.service.response.NewsReplyListResponse;
import org.myteam.server.news.newsReply.repository.NewsReplyLockRepository;
import org.myteam.server.news.newsReply.repository.NewsReplyQueryRepository;
import org.myteam.server.news.newsReply.repository.NewsReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsReplyReadService {

	private final NewsReplyRepository newsReplyRepository;
	private final NewsReplyLockRepository newsReplyLockRepository;
	private final NewsReplyQueryRepository newsReplyQueryRepository;
	private final SecurityReadService securityReadService;

	public NewsReplyListResponse findByNewsCommentId(NewsReplyServiceRequest newsReplyServiceRequest) {
		Page<NewsReplyDto> list = newsReplyQueryRepository.getNewsReplyList(
			newsReplyServiceRequest.getNewsCommentId(),
			securityReadService.getAuthenticatedPublicId(),
			newsReplyServiceRequest.toPageable()
		);

		return NewsReplyListResponse.createResponse(PageCustomResponse.of(list));
	}

	public NewsReply findById(Long newsReplyId) {
		return newsReplyRepository.findById(newsReplyId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_REPLY_NOT_FOUND));
	}

	public NewsReply findByIdLock(Long newsReplyId) {
		return newsReplyLockRepository.findById(newsReplyId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_REPLY_NOT_FOUND));
	}

}
