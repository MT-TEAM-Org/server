package org.myteam.server.board.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.util.redis.RankingCacheReader;
import org.myteam.server.global.util.redis.RedisCountService;
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
        int view = redisCountService.getViewCount(DomainType.BOARD, boardId);
//        int recommend = redisCountService.getRecommendCount(DomainType.BOARD, boardId);
//        return view + recommend;
        return view + 0;
    }
}
