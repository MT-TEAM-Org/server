package org.myteam.server.improvement.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.entity.AdminContentMemo;
import org.myteam.server.admin.entity.AdminImproveChangeLog;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.improvement.dto.request.ImprovementRequest.*;
import org.myteam.server.improvement.dto.response.ImprovementResponse.*;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.myteam.server.global.exception.ErrorCode.USER_NOT_FOUND;

class ImprovementSaveServiceTest extends IntegrationTestSupport {

    @Autowired
    private ImprovementService improvementService;
    private Member member;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        createAdminBot();
        member = createMember(1);
        publicId = member.getPublicId();
    }

    @AfterEach
    void clear() {
        improvementCountRepository.deleteAllInBatch();
        improvementRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("로그인 멤버 개선요청 저장 성공")
    void saveImprovement_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);
        when(redisCountService.getCommonCount(
                eq(ServiceType.CHECK),
                eq(DomainType.IMPROVEMENT),
                anyLong(),
                isNull()
        )).thenReturn(new CommonCountDto(0, 0, 0));
        ImprovementSaveRequest request = new ImprovementSaveRequest(
                "제목",
                "내용",
                null,
                null
        );

        // when
        ImprovementSaveResponse response = improvementService.saveImprovement(request, "127.0.0.1");
        List<AdminImproveChangeLog> adminImproveChangeLogList=adminImproveChangeLogRepo.findAll();
        List<AdminContentMemo> adminContentMemos=adminContentMemoRepo.findAll();
        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.isRecommended()).isFalse();
        assertThat(response.getPreviousId()).isNull();
        assertThat(response.getNextId()).isNull();
        assertThat(adminImproveChangeLogList.size()).isEqualTo(1);
        assertThat(adminContentMemos.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("로그인하지 않은 멤버 개선요청 저장 실패")
    void saveImprovement_success_no_login() {
        // given
        when(securityReadService.getMember()).thenThrow(new PlayHiveException(USER_NOT_FOUND));
        ImprovementSaveRequest request = new ImprovementSaveRequest(
                "제목",
                "내용",
                null,
                null
        );

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            improvementService.saveImprovement(request, "127.0.0.1");
        });


        // then
        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }
}