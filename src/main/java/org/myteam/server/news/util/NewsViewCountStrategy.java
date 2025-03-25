package org.myteam.server.news.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.myteam.server.news.newsCount.repository.NewsCountRepository;
import org.myteam.server.util.ViewCountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsViewCountStrategy implements ViewCountStrategy {

    private final String KEY = "view:news:";
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
    public CommonCount loadFromDatabase(Long contentId) {
        NewsCount newsCount = newsCountRepository.findByNewsId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_NOT_FOUND));
        return new CommonCount(newsCount, newsCount.getViewCount());
    }

    @Override
    @Transactional
    public void updateToDatabase(Long id, int viewCount) {
        newsCountRepository.updateViewCount(id, viewCount);
    }
}
