package org.myteam.server.news.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.news.newsCount.repository.NewsCountRepository;
import org.myteam.server.util.CountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsCountStrategy implements CountStrategy {

    private final String KEY = "news:count:";
    private final NewsCountRepository newsCountRepository;

    @Override
    public String getRedisKey(Long contentId) {
        return KEY + contentId;
    }

    @Override
    public String getRedisPattern() {
        return KEY + "*";
    }

    @Override
    public Long extractContentIdFromKey(String key) {
        return Long.parseLong(key.substring(KEY.length()));
    }

    @Override
    public CommonCount<NewsCount> loadFromDatabase(Long contentId) {
        NewsCount newsCount = newsCountRepository.findByNewsId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_NOT_FOUND));

        return new CommonCount<>(
                newsCount,
                newsCount.getViewCount(),
                newsCount.getCommentCount(),
                newsCount.getRecommendCount()
        );
    }

    @Override
    @Transactional
    public void updateToDatabase(CommonCount<?> count) {
        NewsCount newsCount = (NewsCount) count.getCount();
        Long newsId = newsCount.getNews().getId();

        newsCountRepository.updateAllCounts(
                newsId,
                count.getViewCount(),
                count.getCommentCount(),
                count.getRecommendCount()
        );
    }
}
