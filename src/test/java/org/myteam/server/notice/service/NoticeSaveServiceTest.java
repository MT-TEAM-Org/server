package org.myteam.server.notice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.notice.dto.request.NoticeRequest.*;
import org.myteam.server.notice.dto.response.NoticeResponse.*;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NoticeSaveServiceTest extends IntegrationTestSupport {

    @Autowired
    private NoticeService noticeService;
    @MockBean
    private NoticeRecommendReadService noticeRecommendReadService;

    private Member adminMember;
    private Member normalMember;

    @Transactional
    @BeforeEach
    void setUp() {
        adminMember = createAdmin(1);
        normalMember = createMember(2);
    }

    @Test
    @DisplayName("관리자가 공지사항 저장 요청 시 정상 등록된다")
    void saveNotice_admin_success() {
        // given
        when(securityReadService.getMember()).thenReturn(adminMember);
        when(redisCountService.getCommonCount(
                eq(ServiceType.CHECK),
                eq(DomainType.NOTICE),
                anyLong(),
                isNull()
        )).thenReturn(new CommonCountDto(0, 0, 0));
        when(noticeRecommendReadService.isRecommended(anyLong(), any(UUID.class))).thenReturn(false);

        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title("공지사항 제목")
                .content("공지사항 내용")
                .imgUrl("http://example.com/image.png")
                .link("http://example.com")
                .build();

        // when
        NoticeSaveResponse response = noticeService.saveNotice(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("공지사항 제목");
    }

    @Test
    @DisplayName("일반 사용자가 공지사항 저장 요청 시 UNAUTHORIZED 예외 발생")
    void saveNotice_notAdmin_throwsUnauthorized() {
        // given
        when(securityReadService.getMember()).thenReturn(normalMember);

        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title("공지사항 제목")
                .content("공지사항 내용")
                .imgUrl("http://example.com/image.png")
                .link("http://example.com")
                .build();

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            noticeService.saveNotice(request, "127.0.0.1");
        });

        // then
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 공지사항 저장 요청 시 USER_NOT_FOUND 예외 발생")
    void saveNotice_notLoginUser() {
        // given
        when(securityReadService.getMember()).thenThrow(new PlayHiveException(ErrorCode.USER_NOT_FOUND));

        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title("공지사항 제목")
                .content("공지사항 내용")
                .imgUrl("http://example.com/image.png")
                .link("http://example.com")
                .build();

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            noticeService.saveNotice(request, "127.0.0.1");
        });

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}