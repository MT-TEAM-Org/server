package org.myteam.server.global.util.redis;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.util.ViewCountStrategy;
import org.myteam.server.util.ViewCountStrategyFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class RedisViewCountService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ViewCountStrategyFactory strategyFactory;

    public RedisViewCountService(RedisTemplate<String, String> redisTemplate,
                                 ViewCountStrategyFactory strategyFactory) {
        this.redisTemplate = redisTemplate;
        this.strategyFactory = strategyFactory;
    }

    public BoardCount getViewCount(String type, Long contentId) {
        ViewCountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        String value = redisTemplate.opsForValue().get(key);
        if (value != null) { // cache miss
            int count = Integer.parseInt(value);
            BoardCount countObj = new BoardCount();
            countObj.setViewCount(count);
            return countObj;
        }

        // cache hit
        BoardCount dbValue = strategy.loadFromDatabase(contentId);
        redisTemplate.opsForValue().set(key, String.valueOf(dbValue.getViewCount()));
        return dbValue;
    }

    public void incrementViewCount(String type, Long contentId) {
        ViewCountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);
        redisTemplate.opsForValue().increment(key);
    }
}