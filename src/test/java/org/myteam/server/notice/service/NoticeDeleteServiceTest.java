package org.myteam.server.notice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NoticeDeleteServiceTest extends IntegrationTestSupport {

    @Autowired
    private NoticeService noticeService;

    private Member author;
    private Member other;
    private Member admin;
    private Notice notice;

    @BeforeEach
    void setUp() {
        author = createMember(0);
        other = createAdmin(1);
        admin = createAdmin(2);
    }

    @Transactional
    @Test
    @DisplayName("케이스 1: 작성자가 삭제 요청 → 성공")
    void deleteNotice_by_author_success() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", null);
        when(securityReadService.getMember()).thenReturn(admin);

        // when && then
        assertDoesNotThrow(() -> noticeService.deleteNotice(notice.getId()));
    }

    @Transactional
    @Test
    @DisplayName("케이스 2: 관리자가 삭제 요청 → 성공")
    void deleteNotice_by_admin_success() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", null);
        when(securityReadService.getMember()).thenReturn(admin);

        // when && then
        assertDoesNotThrow(() -> noticeService.deleteNotice(notice.getId()));
    }

    @Test
    @DisplayName("케이스 3: 작성자도 관리자도 아닌 유저가 삭제 요청 → 예외 발생")
    void deleteNotice_by_outsider_throws() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", null);
        when(securityReadService.getMember()).thenReturn(other);

        // when
        PlayHiveException ex = assertThrows(PlayHiveException.class, () ->
                noticeService.deleteNotice(notice.getId()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Transactional
    @Test
    @DisplayName("케이스 4: 작성자가 삭제 요청 → 성공 + 이미지 삭제 호출")
    void deleteNotice_by_author_success_img() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", "test.co.kr");
        when(securityReadService.getMember()).thenReturn(admin);

        // when
        assertDoesNotThrow(() -> noticeService.deleteNotice(notice.getId()));

        // then
        verify(s3Service).deleteFile(notice.getImgUrl()); // 이미지 삭제 확인
        verify(redisCountService).removeCount(DomainType.NOTICE, notice.getId()); // Redis 카운트 삭제
    }

    @Transactional
    @Test
    @DisplayName("케이스 5: 관리자가 삭제 요청 → 성공")
    void deleteNotice_by_admin_success_img() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", "test.co.kr");
        when(securityReadService.getMember()).thenReturn(admin);

        // when && then
        assertDoesNotThrow(() -> noticeService.deleteNotice(notice.getId()));
    }

    @Transactional
    @Test
    @DisplayName("케이스 6: 작성자도 관리자도 아닌 유저가 삭제 요청 → 예외 발생")
    void deleteNotice_by_outsider_throws_img() {
        // given
        notice = createNotice(admin, "Original Title", "Original Content", "test.co.kr");
        when(securityReadService.getMember()).thenReturn(other);

        // when
        PlayHiveException ex = assertThrows(PlayHiveException.class, () ->
                noticeService.deleteNotice(notice.getId()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
    }
}