package org.myteam.server.improvement.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.myteam.server.util.ViewCountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImprovementViewCountStrategy implements ViewCountStrategy {

    private final String KEY = "view:improvement:";
    private final ImprovementCountRepository improvementCountRepository;

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
        ImprovementCount improvementCount = improvementCountRepository.findByImprovementId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_NOT_FOUND));
        return new CommonCount(improvementCount, improvementCount.getViewCount());
    }

    @Override
    @Transactional
    public void updateToDatabase(Long id, int viewCount) {
        improvementCountRepository.updateViewCount(id, viewCount);
    }
}
