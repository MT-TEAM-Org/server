package org.myteam.server.news.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.news.domain.NewsCount;
import org.myteam.server.news.repository.NewsCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsCountReadService {

	private final NewsCountRepository newsCountRepository;

	public NewsCount findByNewsId(Long newsId) {
		return newsCountRepository.findByNewsId(newsId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_COUNT_NOT_FOUND));
	}

}
