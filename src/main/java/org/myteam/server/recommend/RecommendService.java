package org.myteam.server.recommend;

import static org.myteam.server.util.ClientUtils.toInt;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.DomainType;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {
    private final List<RecommendHandler> handlers;
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

        String userId = String.valueOf(member.getPublicId());
        String recommendSetKey = "recommend:users:" + content + ":" + contentId;
        Long updateCount;

        if (actionType == RecommendActionType.RECOMMEND) {
            Long added = redisTemplate.opsForSet().add(recommendSetKey, userId);
            if (added == null || added == 0) {
                throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND);
            }

            updateCount = redisTemplate.opsForHash().increment(redisKey, "recommend", 1);

            try {
                handler.saveRecommendation(contentId, member);
            } catch (DuplicateKeyException e) {
                log.warn("중복 추천 DB insert 발생 (무시): {}", e.getMessage());
            }

        } else {
            Long removed = redisTemplate.opsForSet().remove(recommendSetKey, userId);
            if (removed == null || removed == 0) {
                throw new PlayHiveException(ErrorCode.NOT_RECOMMENDED_YET);
            }

            updateCount = redisTemplate.opsForHash().increment(redisKey, "recommend", -1);
            handler.deleteRecommendation(contentId, member.getPublicId());
        }

        Object view = redisTemplate.opsForHash().get(redisKey, "view");
        Object comment = redisTemplate.opsForHash().get(redisKey, "comment");

        return new CommonCountDto(
                toInt(view),
                toInt(comment),
                updateCount.intValue()
        );
    }
}