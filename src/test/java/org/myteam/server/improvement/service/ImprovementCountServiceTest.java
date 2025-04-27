package org.myteam.server.improvement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ImprovementCountServiceTest extends IntegrationTestSupport {

    @Autowired
    private ImprovementCountService improvementCountService;

    @Test
    @DisplayName("개선 요청 추천 성공")
    void recommendImprovement_success() {
        // given
        Long improvementId = 1L;
        when(redisCountService.getCommonCount(
                eq(ServiceType.RECOMMEND),
                eq(DomainType.IMPROVEMENT),
                eq(improvementId),
                isNull()
        )).thenReturn(new CommonCountDto(0, 0, 0));

        // when & then
        assertThatCode(() ->
                improvementCountService.recommendImprovement(improvementId)
        ).doesNotThrowAnyException();

        // verify
        verify(redisCountService).getCommonCount(
                ServiceType.RECOMMEND,
                DomainType.IMPROVEMENT,
                improvementId,
                null
        );
    }

    @Test
    @DisplayName("개선 요청 추천 취소 성공")
    void deleteRecommendImprovement_success() {
        // given
        Long improvementId = 1L;
        when(redisCountService.getCommonCount(
                eq(ServiceType.RECOMMEND_CANCEL),
                eq(DomainType.IMPROVEMENT),
                eq(improvementId),
                isNull()
        )).thenReturn(new CommonCountDto(0, 0, 0));

        // when & then
        assertThatCode(() ->
                improvementCountService.deleteRecommendImprovement(improvementId)
        ).doesNotThrowAnyException();

        // verify
        verify(redisCountService).getCommonCount(
                ServiceType.RECOMMEND_CANCEL,
                DomainType.IMPROVEMENT,
                improvementId,
                null
        );
    }
}