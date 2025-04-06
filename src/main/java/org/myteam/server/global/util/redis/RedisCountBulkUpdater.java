package org.myteam.server.global.util.redis;

import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.util.CountStrategy;
import org.myteam.server.util.CountStrategyFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisCountBulkUpdater {

    private final RedisTemplate<String, String> redisTemplate;
    private final CountStrategyFactory strategyFactory;

    public RedisCountBulkUpdater(RedisTemplate<String, String> redisTemplate,
                                 CountStrategyFactory strategyFactory) {
        this.redisTemplate = redisTemplate;
        this.strategyFactory = strategyFactory;
    }

    /**
     * Redis에 저장된 count 정보 → DB로 벌크 업데이트
     */
    public void bulkUpdate(String type) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String pattern = strategy.getRedisPattern();

        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            log.info("📭 [카운트 벌크 업데이트] 대상 없음 - type: {}", type);
            return;
        }

        for (String key : keys) {
            try {
                Long contentId = strategy.extractContentIdFromKey(key);
                Map<Object, Object> redisHash = redisTemplate.opsForHash().entries(key);

                int viewCount = Integer.parseInt(redisHash.getOrDefault("view", "0").toString());
                int commentCount = Integer.parseInt(redisHash.getOrDefault("comment", "0").toString());

                // 💡 contentId를 기반으로 DB에서 객체 가져오기
                CommonCount<?> count = strategy.loadFromDatabase(contentId);
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
}