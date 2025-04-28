package org.myteam.server.notice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.notice.domain.Notice;
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
import static org.myteam.server.global.exception.ErrorCode.INVALID_TYPE;
import static org.myteam.server.global.exception.ErrorCode.USER_NOT_FOUND;

class NoticeUpdateServiceTest extends IntegrationTestSupport {

    @Autowired
    private NoticeService noticeService;
    @MockBean
    private NoticeRecommendReadService noticeRecommendReadService;

    private Member admin;
    private Member other;
    private Notice notice;

    @Transactional
    @BeforeEach
    void setUp() {
        admin = createAdmin(1);
        other = createMember(2);
    }

    @Transactional
    @Test
    @DisplayName("케이스 1: 관리자가 공지사항을 정상 수정한다 (추천 true)")
    void updateNotice_admin_success_recommend_true() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", null);
        when(securityReadService.getMember()).thenReturn(admin);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NOTICE, notice.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(noticeRecommendReadService.isRecommended(notice.getId(), admin.getPublicId())).thenReturn(true);

        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .imgUrl("http://example.com/updated.png")
                .link("http://example.com/updated")
                .build();

        // when
        NoticeSaveResponse response = noticeService.updateNotice(request, notice.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("Updated Title");
        assertThat(response.isRecommended()).isTrue();
    }

    @Transactional
    @Test
    @DisplayName("케이스 2: 관리자가 공지사항을 수정할 때 추천 false")
    void updateNotice_admin_success_recommend_false() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", null);
        when(securityReadService.getMember()).thenReturn(admin);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NOTICE, notice.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(noticeRecommendReadService.isRecommended(notice.getId(), admin.getPublicId())).thenReturn(false);

        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title("Updated Title 2")
                .content("Updated Content 2")
                .imgUrl("http://example.com/updated2.png")
                .link("http://example.com/updated2")
                .build();

        // when
        NoticeSaveResponse response = noticeService.updateNotice(request, notice.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("Updated Title 2");
        assertThat(response.isRecommended()).isFalse();
    }

    @Test
    @DisplayName("케이스 3: 관리자가 아닌 일반 유저가 수정 시 UNAUTHORIZED 예외 발생")
    void updateNotice_notAdmin_shouldThrow() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", null);
        when(securityReadService.getMember()).thenReturn(other);

        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title("Illegal Update")
                .content("Should not succeed")
                .imgUrl(null)
                .link(null)
                .build();

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () ->
                noticeService.updateNotice(request, notice.getId())
        );

        // then
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @DisplayName("케이스 4: 작성자가 아닌 관리자가 아닌 경우 UNAUTHORIZED 예외 발생")
    void updateNotice_notAuthor_shouldThrow() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", null);
        Member wrongAdmin = createAdmin(3); // 다른 admin
        when(securityReadService.getMember()).thenReturn(wrongAdmin);

        NoticeSaveRequest request = NoticeSaveRequest.builder()
                .title("Illegal Update 2")
                .content("Should fail again")
                .imgUrl(null)
                .link(null)
                .build();

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () ->
                noticeService.updateNotice(request, notice.getId())
        );

        // then
        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }
}