package org.myteam.server.news.service;

import org.myteam.server.news.dto.controller.response.NewsListResponse;
import org.myteam.server.news.repository.NewsQueryRepository;
import org.myteam.server.news.dto.service.response.NewsDto;
import org.myteam.server.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.global.page.response.PageCustom;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsReadService {

	private final NewsQueryRepository newsQueryRepository;

	public NewsListResponse findAll(NewsServiceRequest newsServiceRequest) {
		Page<NewsDto> newsPagingList = newsQueryRepository.getNewsList(
				newsServiceRequest.getCategory(),
				newsServiceRequest.getOrderType(),
				newsServiceRequest.toPageable()
			);

		return NewsListResponse.of(PageCustom.of(newsPagingList));
	}

}
