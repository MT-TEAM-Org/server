package org.myteam.server.news.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.news.domain.News;
import org.myteam.server.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.repository.NewsQueryRepository;
import org.myteam.server.news.dto.repository.NewsDto;
import org.myteam.server.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.news.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsReadService {

	private final NewsQueryRepository newsQueryRepository;
	private final NewsRepository newsRepository;

	public NewsListResponse findAll(NewsServiceRequest newsServiceRequest) {
		Page<NewsDto> newsPagingList = newsQueryRepository.getNewsList(
				newsServiceRequest.getCategory(),
				newsServiceRequest.getOrderType(),
				newsServiceRequest.toPageable()
			);

		return NewsListResponse.createResponse(PageCustomResponse.of(newsPagingList));
	}

	public News findById(Long newsId) {
		return newsRepository.findById(newsId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_NOT_FOUND));
	}

}
