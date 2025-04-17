package org.myteam.server.board.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.RankingCacheReader;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisBoardRankingReader implements RankingCacheReader {

    private final RedisCountService redisCountService;

    @Override
    public int getViewCount(Long boardId) {
        return redisCountService.getViewCount(DomainType.BOARD, boardId);
    }

    @Override
    public int getTotalScore(Long boardId) {
        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD, boardId,
                null);
        return commonCountDto.getViewCount() + commonCountDto.getRecommendCount();
    }
}
