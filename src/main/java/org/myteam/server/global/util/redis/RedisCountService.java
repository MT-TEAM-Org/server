package org.myteam.server.global.util.redis;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.recommend.RecommendActionType;
import org.myteam.server.recommend.RecommendService;
import org.myteam.server.util.CountStrategy;
import org.myteam.server.util.CountStrategyFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static org.myteam.server.util.ClientUtils.toInt;

@Service
@Slf4j
public class RedisCountService {

    enum ServiceType {
        VIEW, COMMENT, RECOMMEND, RECOMMEND_CANCEL, NORMAL
    }

    private static final long EXPIRED_TIME = 5L; // 조회수 만료 시간. 5분보다 큰 값으로 설정

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final CountStrategyFactory strategyFactory;
    private final RecommendService recommendService;

    public RedisCountService(RedissonClient redissonClient,
                             RedisTemplate<String, String> redisTemplate,
                             CountStrategyFactory strategyFactory,
                             RecommendService recommendService) {
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
        this.strategyFactory = strategyFactory;
        this.recommendService = recommendService;
    }

    /**
     * 네이티브한 키 값.
     */
    public String getRedisKey(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        return strategy.getRedisKey(contentId);
    }

    /**
     * 각 서비스에서 호출하는 함수.
     * TODO: redisTemplate 타입 변경 RedisTemplate<String, Object>
     * @param type: 레디스를 호출하는 목적("view", "comment", "recommend", "normal")
     * @param content: 어떤 게시판인지("board", "news" ...)
     * @param contentId: 각 게시판의 id
     * @return
     */
    public CommonCountDto getCommonCount(String type, String content, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(content);
        String key = strategy.getRedisKey(contentId);

        // Redis 해시 조회
        Map<Object, Object> redisMap = redisTemplate.opsForHash().entries(key);
        Integer viewCount, commentCount, recommendCount;

        if (redisMap == null || redisMap.isEmpty()) { // cache miss
            CommonCount<?> dbValue = strategy.loadFromDatabase(contentId);

            viewCount = dbValue.getViewCount();
            commentCount = dbValue.getCommentCount();
            recommendCount = dbValue.getRecommendCount();

            redisTemplate.opsForHash().putAll(key, Map.of(
                    "view", viewCount,
                    "comment", commentCount,
                    "recommend", recommendCount
            ));
            redisTemplate.expire(key, Duration.ofMinutes(EXPIRED_TIME));
        } else { // cache hit
            viewCount = toInt(redisMap.get("view"));
            commentCount = toInt(redisMap.get("comment"));
            recommendCount = toInt(redisMap.get("recommend"));
        }

        if (type.equals(ServiceType.VIEW.name())) {
            /**
             * 조회할 때. 조회할 시 + 1
             */
            Long updateCount = redisTemplate.opsForHash().increment(key, "view", 1);

            return new CommonCountDto(updateCount.intValue(), commentCount, recommendCount);
        } else if (type.equals(ServiceType.COMMENT.name())) {
            /**
             * 댓글 쓸 때. 댓글 쓸 시 + 1
             */
            Long updateCount = redisTemplate.opsForHash().increment(key, "comment", 1);

            return new CommonCountDto(viewCount, updateCount.intValue(), recommendCount);
        } else if (type.equals(ServiceType.RECOMMEND.name())) {
            /**
             * 추천할 때. 추천할 시 + 1
             * TODO 여기서 분산락 적용하면 됨.
             */
            return recommendService.handleRecommend(content, contentId, RecommendActionType.RECOMMEND, key);
        } else if (type.equals(ServiceType.RECOMMEND_CANCEL.name())) {
            /**
             * 추천 취소
             */
            return recommendService.handleRecommend(content, contentId, RecommendActionType.CANCEL, key);
        }
        return new CommonCountDto(viewCount, commentCount, recommendCount);
    }

    /**
     * 조회수 조회
     */
    public int getViewCount(String type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return Integer.parseInt(value);
        }

        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getViewCount();
        redisTemplate.opsForValue().set(key, String.valueOf(newCount), Duration.ofMinutes(EXPIRED_TIME));
        return newCount;
    }

    /**
     * 특정 키 값 조회 + 조회수 증가
     */
    public int getViewCountAndIncr(String type, Long contentId) {
        System.out.println("RedisCountService.getViewCountAndIncr");
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        Object value = redisTemplate.opsForHash().get(key, "view");
        if (value != null) { // cache hit
            // TODO: 키로 바꾸기
            Long newValue = redisTemplate.opsForHash().increment(key, "view", 1);
            System.out.println("newViewValue = " + newValue);
            return newValue.intValue();
        }

        // cache miss
        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getViewCount() + 1;

        System.out.println("newViewCount = " + newCount);

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
            System.out.println("newCommentValue = " + newValue);
            return newValue.intValue();
        }

        // cache miss
        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getCommentCount() + 1;

        System.out.println("newCommentCount = " + newCount);

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