package org.myteam.server.global.util.redis;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.util.CountStrategy;
import org.myteam.server.util.CountStrategyFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisCountService {
    private static final long EXPIRED_TIME = 5L; // 조회수 만료 시간. 5분보다 큰 값으로 설정

    private final RedisTemplate<String, String> redisTemplate;
    private final CountStrategyFactory strategyFactory;

    public RedisCountService(RedisTemplate<String, String> redisTemplate,
                             CountStrategyFactory strategyFactory) {
        this.redisTemplate = redisTemplate;
        this.strategyFactory = strategyFactory;
    }

    /**
     * 네이티브한 키 값.
     */
    public String getRedisKey(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        return strategy.getRedisKey(contentId);
    }

    /**
     * 조회수 조회
     */
    public int getViewCount(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        Object value = redisTemplate.opsForHash().get(key, "view");
        if (value != null) { // cache hit
            // TODO: 키로 바꾸기
            Long newValue = redisTemplate.opsForHash().increment(key, "view", 1);
            return newValue.intValue();
        }

        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getViewCount();

        redisTemplate.opsForHash().put(key, "view", String.valueOf(newCount));
        redisTemplate.expire(key, Duration.ofMinutes(EXPIRED_TIME));
        return newCount;
    }

    /**
     * 특정 키 값 조회 + 조회수 증가
     */
    public int getViewCountAndIncr(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        Object value = redisTemplate.opsForHash().get(key, "view");
        if (value != null) { // cache hit
            // TODO: 키로 바꾸기
            Long newValue = redisTemplate.opsForHash().increment(key, "view", 1);
            return newValue.intValue();
        }

        // cache miss
        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getViewCount() + 1;

        redisTemplate.opsForHash().put(key, "view", String.valueOf(newCount));
        redisTemplate.expire(key, Duration.ofMinutes(EXPIRED_TIME));
        return newCount;
    }

    /**
     * 특정 키 값 조회 + 댓글수 증가
     */
    public int getCommentCountAndIncr(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        Object value = redisTemplate.opsForHash().get(key, "comment");
        if (value != null) { // cache hit
            // TODO: 키로 바꾸기
            Long newValue = redisTemplate.opsForHash().increment(key, "comment", 1);
            return newValue.intValue();
        }

        // cache miss
        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getCommentCount() + 1;

        redisTemplate.opsForHash().put(key, "comment", String.valueOf(newCount));
        redisTemplate.expire(key, Duration.ofMinutes(EXPIRED_TIME));
        return newCount;
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);
        redisTemplate.opsForHash().increment(key, "view", 1);
    }

    /**
     * 댓글 수 증가
     */
    public void incrementCommentCount(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);
        redisTemplate.opsForHash().increment(key, "comment", 1);
    }

    /**
     * 특정 키 삭제 (전체 Hash 삭제)
     */
    public void removeViewCount(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);
        redisTemplate.delete(key);
    }
}