package org.myteam.server.board.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

class BoardCountServiceTest extends IntegrationTestSupport {

    @Autowired
    private BoardCountService boardCountService;

    private final Long boardId = 1L;

    @Test
    @DisplayName("게시글 추천 처리 시 RECOMMEND 요청이 RedisCountService에 전달된다.")
    void recommendBoard_호출시_RECOMMEND() {
        // when
        boardCountService.recommendBoard(boardId);

        // then
        verify(redisCountService).getCommonCount(
                eq(ServiceType.RECOMMEND),
                eq(DomainType.BOARD),
                eq(boardId),
                isNull()
        );
    }

    @Test
    @DisplayName("게시글 추천 취소 처리 시 RECOMMEND_CANCEL 요청이 RedisCountService에 전달된다.")
    void deleteRecommendBoard_호출시_RECOMMEND_CANCEL() {
        // when
        boardCountService.deleteRecommendBoard(boardId);

        // then
        verify(redisCountService).getCommonCount(
                eq(ServiceType.RECOMMEND_CANCEL),
                eq(DomainType.BOARD),
                eq(boardId),
                isNull()
        );
    }
}