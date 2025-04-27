package org.myteam.server.improvement.service;

import org.assertj.core.api.IntegerAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.domain.ImprovementRecommend;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.improvement.dto.request.ImprovementRequest.*;
import org.myteam.server.improvement.dto.response.ImprovementResponse.*;
import org.myteam.server.improvement.repository.ImprovementQueryRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.myteam.server.global.exception.ErrorCode.USER_NOT_FOUND;

class ImprovementDeleteServiceTest extends IntegrationTestSupport {

    @Autowired
    private ImprovementService improvementService;

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
    @DisplayName("케이스 1: 작성자가 삭제 요청 → 성공")
    void deleteImprovement_by_author_success() {
        // given
        when(securityReadService.getMember()).thenReturn(author);

        // when && then
        assertDoesNotThrow(() -> improvementService.deleteImprovement(improvement.getId()));
    }

    @Test
    @DisplayName("케이스 2: 작성자가 아니고 관리자도 아닌 경우 → 예외 발생")
    void deleteImprovement_by_outsider_throws() {
        // given
        when(securityReadService.getMember()).thenReturn(other);

        // when
        PlayHiveException ex = assertThrows(PlayHiveException.class, () ->
                improvementService.deleteImprovement(improvement.getId())
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("케이스 3: 삭제할 개선 요청에 이미지가 있을 경우 → S3 이미지 삭제 호출")
    void deleteImprovement_with_image_success() {
        // given
        Improvement imgImprovement = createImprovement(author, true);
        when(securityReadService.getMember()).thenReturn(author);

        // when
        assertDoesNotThrow(() -> improvementService.deleteImprovement(imgImprovement.getId()));

        // then
        verify(s3Service).deleteFile(imgImprovement.getImgUrl());
    }
}