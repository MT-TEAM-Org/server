package org.myteam.server.global.util.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.util.ViewCountStrategy;
import org.myteam.server.util.ViewCountStrategyFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class RedisViewCountBulkUpdater {

    private final RedisTemplate<String, String> redisTemplate;
    private final ViewCountStrategyFactory strategyFactory;

    /**
     * 특정 타입 전체 조회수에 대해 Redis → DB 벌크 업데이트
     */
    public void bulkUpdate(String type) {
        ViewCountStrategy strategy = strategyFactory.getStrategy(type);
        String pattern = strategy.getRedisPattern();

        // Redis에 저장된 모든 조회수 키 가져오기
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("[조회수 벌크 업데이트] 대상 없음 - type: {}", type);
            return;
        }

        for (String key : keys) {
            try {
                String value = redisTemplate.opsForValue().get(key);
                if (value == null) {
                    redisTemplate.delete(key);
                }

                int viewCount = Integer.parseInt(value);
                Long contentId = strategy.extractContentIdFromKey(key);

                /**
                 * @Brief: DB 벌크 업데이트
                 */
                strategy.updateToDatabase(contentId, viewCount);
                /**
                 * @Brief: 레디스 키 삭제
                 */
                redisTemplate.delete(key);

                log.info("✅ [조회수 DB 저장 완료] type={}, id={}, count={}", type, contentId, viewCount);
            } catch (Exception e) {
                log.error("❌ [조회수 저장 실패] key: {}, 이유: {}", key, e.getMessage());
            }
        }
    }
}
