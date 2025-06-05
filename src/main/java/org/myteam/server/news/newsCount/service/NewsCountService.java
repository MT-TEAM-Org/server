package org.myteam.server.news.newsCount.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.news.newsCount.dto.service.response.NewsRecommendResponse;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsCountService {

    private final RedisCountService redisCountService;

    public NewsRecommendResponse recommendNews(Long newsId) {

        redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.NEWS, newsId, null);

        return NewsRecommendResponse.createResponse(newsId);
    }

    public NewsRecommendResponse cancelRecommendNews(Long newsId) {

        redisCountService.getCommonCount(ServiceType.RECOMMEND_CANCEL, DomainType.NEWS, newsId, null);

        return NewsRecommendResponse.createResponse(newsId);
    }
}
