package org.myteam.server.global.util.redis;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.util.ViewCountStrategy;
import org.myteam.server.util.ViewCountStrategyFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisViewCountService {
    private static final long EXPIRED_TIME = 5L; // 조회수 만료 시간. 5분보다 큰 값으로 설정

    private final RedisTemplate<String, String> redisTemplate;
    private final ViewCountStrategyFactory strategyFactory;

    public RedisViewCountService(RedisTemplate<String, String> redisTemplate,
                                 ViewCountStrategyFactory strategyFactory) {
        this.redisTemplate = redisTemplate;
        this.strategyFactory = strategyFactory;
    }

    /**
     * 특정 키 값 조회
     */
    public int getViewCount(String type, Long contentId) {
        ViewCountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return Integer.parseInt(value);
        }

        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getViewCount();
        redisTemplate.opsForValue().set(key, String.valueOf(newCount), EXPIRED_TIME);
        return newCount;
    }

    /**
     * 특정 키 값 조회 + 조회수 증가
     */
    public int getViewCountAndIncr(String type, Long contentId) {
        ViewCountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        String value = redisTemplate.opsForValue().get(key);
        if (value != null) { // cache hit
            // TODO: 키로 바꾸기
            incrementViewCount(type, contentId);
            return Integer.parseInt(value) + 1;
        }

        // cache miss
        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getViewCount() + 1;
        redisTemplate.opsForValue().set(key, String.valueOf(newCount), Duration.ofMinutes(EXPIRED_TIME));
        return newCount;
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount(String type, Long contentId) {
        ViewCountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);
        redisTemplate.opsForValue().increment(key);
    }
}