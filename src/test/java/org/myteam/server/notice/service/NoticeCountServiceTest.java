package org.myteam.server.notice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

class NoticeCountServiceTest extends IntegrationTestSupport {

    @Autowired
    private NoticeCountService noticeCountService;

    private final Long noticeId = 1L;

    @Test
    @DisplayName("공지사항 추천 처리 시 RECOMMEND 요청이 RedisCountService에 전달된다.")
    void recommendBoard_호출시_RECOMMEND() {
        // when
        noticeCountService.recommendNotice(noticeId);

        // then
        verify(redisCountService).getCommonCount(
                eq(ServiceType.RECOMMEND),
                eq(DomainType.NOTICE),
                eq(noticeId),
                isNull()
        );
    }

    @Test
    @DisplayName("공지사항 추천 취소 처리 시 RECOMMEND_CANCEL 요청이 RedisCountService에 전달된다.")
    void deleteRecommendBoard_호출시_RECOMMEND_CANCEL() {
        // when
        noticeCountService.deleteRecommendNotice(noticeId);

        // then
        verify(redisCountService).getCommonCount(
                eq(ServiceType.RECOMMEND_CANCEL),
                eq(DomainType.NOTICE),
                eq(noticeId),
                isNull()
        );
    }
}