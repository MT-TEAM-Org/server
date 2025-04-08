package org.myteam.server.news.newsCount.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.service.CommentCountService;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.news.newsCount.dto.service.response.NewsRecommendResponse;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsCountService implements CommentCountService {

    private final NewsCountReadService newsCountReadService;
    private final RedisCountService redisCountService;

    public NewsRecommendResponse recommendNews(Long newsId) {

        redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.NEWS, newsId, null);

        return NewsRecommendResponse.createResponse(newsId);
    }

    public NewsRecommendResponse cancelRecommendNews(Long newsId) {

        redisCountService.getCommonCount(ServiceType.RECOMMEND_CANCEL, DomainType.NEWS, newsId, null);

        return NewsRecommendResponse.createResponse(newsId);
    }

    public void addRecommendCount(Long newsId) {
        newsCountReadService.findByNewsIdLock(newsId).addRecommendCount();
    }

    public void minusRecommendCount(Long newsId) {
        newsCountReadService.findByNewsIdLock(newsId).minusRecommendCount();
    }

    @Override
    public void addCommentCount(Long newsId) {
        newsCountReadService.findByNewsIdLock(newsId).addCommentCount();
    }

    @Override
    public void minusCommentCount(Long newsId) {
        newsCountReadService.findByNewsIdLock(newsId).minusCommentCount();
    }

    @Override
    public void minusCommentCount(Long newsId, int minusCount) {
        newsCountReadService.findByNewsIdLock(newsId).minusCommentCount(minusCount);
    }

    public void addViewCount(Long newsId) {
        newsCountReadService.findByNewsIdLock(newsId).addViewCount();
    }

    public void minusViewCont(Long newsId) {
        newsCountReadService.findByNewsIdLock(newsId).minusViewCount();
    }

}
