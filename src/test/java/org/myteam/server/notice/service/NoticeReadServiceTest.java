package org.myteam.server.notice.service;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.dto.request.NoticeRequest.*;
import org.myteam.server.notice.dto.response.NoticeResponse.*;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
class NoticeReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private NoticeReadService noticeReadService;

    @Autowired
    NoticeService noticeService;

    private List<Notice> noticeList = new ArrayList<>();
    private Member admin;

    @BeforeEach
    void setUp() {
        admin = createAdmin(1);
        for (int i = 1; i <= 5; i++) {
            noticeList.add(createNotice(admin, "공지 제목 " + i, "공지" + i, null));
        }
    }

    @Test
    @DisplayName("공지사항 ID로 조회 성공")
    void findById_존재하는_공지사항() {
        // given
        Notice notice = createNotice(admin, "특별 공지", "특별 공지 내용", null);

        // when
        Notice result = noticeReadService.findById(notice.getId());

        // then
        assertThat(result.getId()).isEqualTo(notice.getId());
        assertThat(result.getTitle()).isEqualTo("특별 공지");
    }

    @Test
    @DisplayName("공지사항 ID로 조회 성공 - 로그인하지 않을때")
    void findById_존재하는_공지사항_로그인하지않을때() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);
        when(redisCountService.getCommonCount(
                eq(ServiceType.CHECK),
                eq(DomainType.NOTICE),
                anyLong(),
                isNull()))
                .thenReturn(new CommonCountDto(0, 0, 0));
        Notice notice = createNotice(admin, "특별 공지", "특별 공지 내용", null);

        // when
        NoticeSaveResponse result = noticeReadService.getNotice(notice.getId());

        // then
        assertThat(result.getContent()).isEqualTo("특별 공지 내용");
        assertThat(result.getTitle()).isEqualTo("특별 공지");
    }

    @Test
    @DisplayName("공지사항 ID로 조회 성공 - 로그인")
    void findById_존재하는_공지사항_로그인() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(admin.getPublicId());
        when(redisCountService.getCommonCount(
                eq(ServiceType.CHECK),
                eq(DomainType.NOTICE),
                anyLong(),
                isNull()))
                .thenReturn(new CommonCountDto(0, 0, 0));
        Notice notice = createNotice(admin, "특별 공지", "특별 공지 내용", null);

        // when
        NoticeSaveResponse result = noticeReadService.getNotice(notice.getId());

        // then
        assertThat(result.getContent()).isEqualTo("특별 공지 내용");
        assertThat(result.getTitle()).isEqualTo("특별 공지");
    }

    @Test
    @DisplayName("공지사항 ID로 조회 실패 - 예외 발생")
    void findById_없는_공지사항() {
        // given && when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            noticeReadService.findById(9999L);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOTICE_NOT_FOUND);
    }

    @Test
    @DisplayName("공지사항 상세 조회 성공")
    void getNotice_상세조회_성공() {
        // given
        Notice notice = noticeList.get(0);

        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NOTICE, notice.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));

        // when
        NoticeSaveResponse response = noticeReadService.getNotice(notice.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(notice.getTitle());
    }

    @Test
    @DisplayName("공지사항 목록 조회 성공")
    void getNoticeList_정상조회() {
        // given
        for (int i = 0; i < noticeList.size(); i++) {
            when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.NOTICE, noticeList.get(i).getId(), null))
                    .thenReturn(new CommonCountDto(0, 0, 0));
        }
        NoticeServiceRequest request = NoticeServiceRequest.builder()
                .searchType(null)
                .search(null)
                .page(1)
                .size(10)
                .build();

        // when
        NoticeListResponse response = noticeReadService.getNoticeList(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList().getContent()).hasSize(5);
        assertThat(response.getList().getContent())
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "공지 제목 1", "공지 제목 2", "공지 제목 3", "공지 제목 4", "공지 제목 5"
                );
    }

    @Test
    @DisplayName("공지사항 존재 여부 확인 - true")
    void existsById_true() {
        // given && when
        boolean exists = noticeReadService.existsById(noticeList.get(0).getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("공지사항 존재 여부 확인 - false")
    void existsById_false() {
        // given && when
        boolean exists = noticeReadService.existsById(9999L);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("관리자 공지사항 목록 조회")
    void adminNoticeListCheck() {
        // given && when
        AdminRequestNotice adminRequestNotice=AdminRequestNotice
                .builder()
                .offset(1)
                .build();

       List<AdminNoticeResponse> adminNoticeResponses=
               noticeReadService.adminGetNoticeList(adminRequestNotice).getContent();

        // then
        assertThat(adminNoticeResponses.size()).isEqualTo(5);
    }
}