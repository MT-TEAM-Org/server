package org.myteam.server.improvement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.improvement.dto.request.ImprovementRequest.*;
import org.myteam.server.improvement.dto.response.ImprovementResponse.*;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ImprovementUpdateServiceTest extends IntegrationTestSupport {

    @Autowired
    private ImprovementReadService improvementReadService;
    @Autowired
    private ImprovementService improvementService;
    @MockBean
    private ImprovementRecommendReadService improvementRecommendReadService;

    private Member author;
    private Member other;
    private Member admin;
    private UUID publicId;
    private Improvement improvement;

    @Transactional
    @BeforeEach
    void setUp() {
        author = createMember(0);
        other = createMember(1);
        admin = createAdmin(2);
        publicId = author.getPublicId();
        improvement = createImprovement(author, false);
    }

    @Test
    @DisplayName("케이스 1: 작성자가 수정하고 추천까지 한 경우 → 추천 true")
    void updateImprovement_author_recommend_true() {
        // given
        when(securityReadService.getMember()).thenReturn(author);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT, improvement.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(improvementRecommendReadService.isRecommended(improvement.getId(), author.getPublicId()))
                .thenReturn(true);

        ImprovementSaveRequest request = new ImprovementSaveRequest(
                "수정된 제목",
                "수정된 내용",
                null,
                null
        );

        // when
        ImprovementSaveResponse response = improvementService.updateImprovement(request, improvement.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.isRecommended()).isTrue();
    }

    @Test
    @DisplayName("케이스 2: 작성자가 수정했지만 추천 안 한 경우 → 추천 false")
    void updateImprovement_author_recommend_false() {
        // given
        when(securityReadService.getMember()).thenReturn(author);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT, improvement.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(improvementRecommendReadService.isRecommended(improvement.getId(), author.getPublicId()))
                .thenReturn(false);

        ImprovementSaveRequest request = new ImprovementSaveRequest(
                "다른 제목",
                "다른 내용",
                null,
                null
        );

        // when
        ImprovementSaveResponse response = improvementService.updateImprovement(request, improvement.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("다른 제목");
        assertThat(response.isRecommended()).isFalse();
    }

    @Test
    @DisplayName("케이스 3: 이미지가 변경된 경우 → S3 삭제 호출")
    void updateImprovement_image_changed() {
        // given
        when(securityReadService.getMember()).thenReturn(author);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT, improvement.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(improvementRecommendReadService.isRecommended(improvement.getId(), author.getPublicId()))
                .thenReturn(false);

        ImprovementSaveRequest request = new ImprovementSaveRequest(
                "제목 변경",
                "내용 변경",
                "https://new-image.com/new.png",
                null
        );

        try (MockedStatic<MediaUtils> utilities = mockStatic(MediaUtils.class)) {
            utilities.when(() -> MediaUtils.verifyImageUrlAndRequestImageUrl(improvement.getImgUrl(), request.getImgUrl()))
                    .thenReturn(true);

            // when
            ImprovementSaveResponse response = improvementService.updateImprovement(request, improvement.getId());

            // then
            assertThat(response.getTitle()).isEqualTo("제목 변경");
            verify(s3Service).deleteFile(improvement.getImgUrl());
        }
    }

    @Test
    @DisplayName("케이스 4: 작성자가 아닌 경우 → 예외 발생")
    void updateImprovement_not_author_shouldThrow() {
        // given
        when(securityReadService.getMember()).thenReturn(other);

        ImprovementSaveRequest request = new ImprovementSaveRequest(
                "잘못된 수정",
                "잘못된 수정",
                null,
                null
        );

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            improvementService.updateImprovement(request, improvement.getId());
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @Test
    @DisplayName("케이스 5: 관리자가 status를 변경")
    void updateImprovementState_admin_success() {
        // given
        when(securityReadService.getMember()).thenReturn(admin);

        // when
        improvementService.updateImprovementStatus(improvement.getId(), ImprovementStatus.RECEIVED);
        Improvement newImprovement = improvementReadService.findById(improvement.getId());

        // then
        assertThat(newImprovement.getImprovementStatus()).isEqualTo(ImprovementStatus.RECEIVED);
    }

    @Transactional
    @Test
    @DisplayName("케이스 5: 멤버가 status를 변경 - 테스트실패")
    void updateImprovementState_member_fail() {
        // given
        when(securityReadService.getMember()).thenReturn(author);

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            improvementService.updateImprovementStatus(improvement.getId(), ImprovementStatus.RECEIVED);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
    }
}