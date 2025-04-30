package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardCountService {

    private final RedisCountService redisCountService;

    public void recommendBoard(Long boardId) {
        redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.BOARD, boardId, null);
    }

    public void deleteRecommendBoard(Long boardId) {
        redisCountService.getCommonCount(ServiceType.RECOMMEND_CANCEL, DomainType.BOARD, boardId, null);
    }
}