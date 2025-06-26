package org.myteam.server.global.util.redis.service;

import static org.myteam.server.util.ClientUtils.toInt;

import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.util.redis.CommonCount;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.recommend.RecommendActionType;
import org.myteam.server.recommend.RecommendService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.util.CountStrategy;
import org.myteam.server.util.CountStrategyFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisCountService {

    private static final long EXPIRED_TIME = 5L; // 조회수 만료 시간. 5분보다 큰 값으로 설정

    private final RedisTemplate<String, Object> redisTemplate;
    private final CountStrategyFactory strategyFactory;
    private final RecommendService recommendService;

    /**
     * 각 서비스에서 호출하는 함수.
     *
     * @param type:      레디스를 호출하는 목적("view", "comment", "recommend", "normal")
     * @param content:   어떤 게시판인지("board", "news" ...)
     * @param contentId: 각 게시판의 id
     * @return
     */
    public CommonCountDto getCommonCount(ServiceType type, DomainType content, Long contentId, Integer minusCount) {
        CountStrategy strategy = strategyFactory.getStrategy(content);
        String key = strategy.getRedisKey(contentId);

        // Redis 해시 조회
        Map<Object, Object> redisMap = redisTemplate.opsForHash().entries(key);
        Integer viewCount, commentCount, recommendCount;

        if (redisMap == null || redisMap.isEmpty()) { // cache miss
            log.info("cache miss type: {}, id: {}", content, contentId);
            CommonCount<?> dbValue = strategy.loadFromDatabase(contentId);

            viewCount = dbValue.getViewCount();
            commentCount = dbValue.getCommentCount();
            recommendCount = dbValue.getRecommendCount();

            redisTemplate.opsForHash().putAll(key, Map.of(
                    "view", String.valueOf(viewCount),
                    "comment", String.valueOf(commentCount),
                    "recommend", String.valueOf(recommendCount)
            ));
            redisTemplate.expire(key, Duration.ofMinutes(EXPIRED_TIME));
        } else { // cache hit
            log.info("cache hit type: {}, id: {}", content, contentId);
            viewCount = toInt(redisMap.get("view"));
            commentCount = toInt(redisMap.get("comment"));
            recommendCount = toInt(redisMap.get("recommend"));
        }

        switch (type) {
            case CHECK:
                return new CommonCountDto(viewCount, commentCount, recommendCount);
            /**
             * 조회할 때. 조회할 시 + 1
             */
            case VIEW:
                Long updateViewCount = redisTemplate.opsForHash().increment(key, "view", 1);
                return new CommonCountDto(updateViewCount.intValue(), commentCount, recommendCount);
            /**
             * 댓글 쓸 때. 댓글 쓸 시 + 1
             */
            case COMMENT:
                Long updateCommentCount = redisTemplate.opsForHash().increment(key, "comment", 1);
                return new CommonCountDto(viewCount, updateCommentCount.intValue(), recommendCount);
            /**
             * 댓글 삭제 할 때, 댓글 삭제 시 -1
             */
            case COMMENT_REMOVE:
                Long updateCommentRemoveCount = redisTemplate.opsForHash().increment(key, "comment", -minusCount);
                return new CommonCountDto(viewCount, updateCommentRemoveCount.intValue(), recommendCount);
            /**
             * 추천할 때. 추천할 시 + 1
             */
            case RECOMMEND:
                return recommendService.handleRecommend(content, contentId, RecommendActionType.RECOMMEND, key);
            /**
             * 추천 취소
             */
            case RECOMMEND_CANCEL:
                return recommendService.handleRecommend(content, contentId, RecommendActionType.CANCEL, key);
            default:
                return new CommonCountDto(viewCount, commentCount, recommendCount);
        }
    }

    /**
     * 조회수 조회
     */
    public int getViewCount(DomainType type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        Object value = redisTemplate.opsForHash().get(key, "view");
        if (value != null) {
            return Integer.parseInt(value.toString());
        }

        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getViewCount();

        redisTemplate.opsForHash().put(key, "view", String.valueOf(newCount));
        redisTemplate.expire(key, Duration.ofMinutes(EXPIRED_TIME));
        return newCount;
    }

    /**
     * 댓글수 조회
     */
    public int getCommentCount(DomainType type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String key = strategy.getRedisKey(contentId);

        Object value = redisTemplate.opsForHash().get(key, "comment");
        if (value != null) {
            return Integer.parseInt(value.toString());
        }

        CommonCount dbValue = strategy.loadFromDatabase(contentId);
        int newCount = dbValue.getCommentCount();

        redisTemplate.opsForHash().put(key, "comment", String.valueOf(newCount));
        redisTemplate.expire(key, Duration.ofMinutes(EXPIRED_TIME));
        return newCount;
    }

    /**
     * 특정 키 삭제 (전체 Hash 삭제)
     */
    public void removeCount(DomainType type, Long contentId) {
        CountStrategy strategy = strategyFactory.getStrategy(type);
        String countKey = strategy.getRedisKey(contentId);
        String recommendSetKey = "recommend:users:" + type.name().toLowerCase() + ":" + contentId;
        
        redisTemplate.delete(countKey);
        redisTemplate.delete(recommendSetKey);

    }
}