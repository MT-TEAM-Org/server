package org.myteam.server.recommend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.improvement.service.ImprovementReadService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.notice.service.NoticeReadService;
import org.myteam.server.report.domain.DomainType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.myteam.server.util.ClientUtils.toInt;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {
    private final List<RecommendHandler> handlers;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SecurityReadService securityReadService;

    public CommonCountDto handleRecommend(
            DomainType content,
            Long contentId,
            RecommendActionType actionType,
            String redisKey
    ) {
        Member member = securityReadService.getMember();
        RecommendHandler handler = handlers.stream()
                .filter(h -> h.supports(content))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 콘텐츠 타입"));

        String lockKey = "lock:recommend:" + content + ":" + contentId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            log.info("락 시도");
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("잠시 후 다시 시도해주세요.");
            }

            boolean already = handler.isAlreadyRecommended(contentId, member.getPublicId());

            if (actionType == RecommendActionType.RECOMMEND && already) {
                throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND);
            }

            if (actionType == RecommendActionType.CANCEL && !already) {
                throw new PlayHiveException(ErrorCode.NOT_RECOMMENDED_YET);
            }

            Long updateCount;
            if (actionType == RecommendActionType.RECOMMEND) {
                log.info("추천 시도: {}", redisKey);
                handler.saveRecommendation(contentId, member);
                updateCount = redisTemplate.opsForHash().increment(redisKey, "recommend", 1);
                log.info("recommend count: {}", updateCount);
            } else {
                log.info("추천 삭제: {}", redisKey);
                handler.deleteRecommendation(contentId, member.getPublicId());
                updateCount = redisTemplate.opsForHash().increment(redisKey, "recommend", -1);
                log.info("recommend count: {}", updateCount);
            }

            // view/comment 값도 함께 조회하려면 entries() 사용
            Object view = redisTemplate.opsForHash().get(redisKey, "view");
            Object comment = redisTemplate.opsForHash().get(redisKey, "comment");

            return new CommonCountDto(
                    toInt(view),
                    toInt(comment),
                    updateCount.intValue()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 인터럽트", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
