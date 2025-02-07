package org.myteam.server.news.news.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.news.news.dto.service.response.NewsListResponse;
import org.myteam.server.news.news.dto.service.response.NewsResponse;
import org.myteam.server.news.news.repository.NewsQueryRepository;
import org.myteam.server.news.news.repository.NewsRepository;
import org.myteam.server.news.newsCount.service.NewsCountReadService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsReadService {

	private final NewsQueryRepository newsQueryRepository;
	private final NewsRepository newsRepository;
	private final NewsCountReadService newsCountReadService;

	public NewsListResponse findAll(NewsServiceRequest newsServiceRequest) {
		Page<NewsDto> newsPagingList = newsQueryRepository.getNewsList(newsServiceRequest);

		return NewsListResponse.createResponse(PageCustomResponse.of(newsPagingList));
	}

	public NewsResponse findOne(Long newsId) {
		return NewsResponse.createResponse(
			findById(newsId),
			newsCountReadService.findByNewsId(newsId)
		);
	}


	public News findById(Long newsId) {
		return newsRepository.findById(newsId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_NOT_FOUND));
	}

}
