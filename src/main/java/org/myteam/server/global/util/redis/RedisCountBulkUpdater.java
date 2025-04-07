package org.myteam.server.global.util.redis;

import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.util.CountStrategy;
import org.myteam.server.util.CountStrategyFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisCountBulkUpdater {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CountStrategyFactory strategyFactory;

    public RedisCountBulkUpdater(RedisTemplate<String, Object> redisTemplate,
                                 CountStrategyFactory strategyFactory) {
        this.redisTemplate = redisTemplate;
        this.strategyFactory = strategyFactory;
    }

    /**
     * Redis에 저장된 count 정보 → DB로 벌크 업데이트
     */
    public void bulkUpdate(DomainType type) {
        System.out.println("RedisCountBulkUpdater.bulkUpdate");
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String pattern = strategy.getRedisPattern();

        // Redis에 저장된 모든 조회수 키 가져오기
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("📭 [카운트 벌크 업데이트] 대상 없음 - type: {}", type);
            return;
        }

        for (String key : keys) {
            try {
                Long contentId = strategy.extractContentIdFromKey(key);
                Map<Object, Object> redisHash = redisTemplate.opsForHash().entries(key);

                if (redisHash == null || redisHash.isEmpty()) {
                    redisTemplate.delete(key);
                    continue;
                }

                // 💡 contentId를 기반으로 DB에서 객체 가져오기
                CommonCount<?> count = strategy.loadFromDatabase(contentId);

                int viewCount = safeParseCount(redisHash.get("view"), count.getViewCount());
                int commentCount = safeParseCount(redisHash.get("comment"), count.getCommentCount());

                System.out.println("viewCount: " + viewCount + " commentCount: " + commentCount);

                count = new CommonCount<>(count.getCount(), viewCount, commentCount);

                strategy.updateToDatabase(count);

                redisTemplate.delete(key);

                log.info("✅ [카운트 DB 저장 완료] type={}, id={}, view={}, comment={}", type, contentId, viewCount,
                        commentCount);

            } catch (Exception e) {
                log.error("❌ [카운트 저장 실패] key: {}, 이유: {}", key, e.getMessage());
            }
        }
    }

    private int safeParseCount(Object redisValue, int fallback) {
        if (redisValue == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(redisValue.toString());
        } catch (NumberFormatException e) {
            log.warn("⚠️ 숫자 파싱 실패 - value: {} (fallback: {})", redisValue, fallback);
            return fallback;
        }
    }
}