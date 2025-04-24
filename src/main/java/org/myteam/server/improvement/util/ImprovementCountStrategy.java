package org.myteam.server.improvement.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.myteam.server.util.CountStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImprovementCountStrategy implements CountStrategy {

    private final String KEY = "improvement:count:";
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
    public CommonCount<ImprovementCount> loadFromDatabase(Long contentId) {
        ImprovementCount improvementCount = improvementCountRepository.findByImprovementId(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_NOT_FOUND));

        return new CommonCount<>(
                improvementCount,
                improvementCount.getViewCount(),
                improvementCount.getCommentCount(),
                improvementCount.getRecommendCount()
        );
    }

    @Override
    @Transactional
    public void updateToDatabase(CommonCount<?> count) {
        ImprovementCount improvementCount = (ImprovementCount) count.getCount();
        Long improvementId = improvementCount.getImprovement().getId();

        improvementCountRepository.updateAllCounts(
                improvementId,
                count.getViewCount(),
                count.getCommentCount(),
                count.getRecommendCount()
        );
    }
}
