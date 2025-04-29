package org.myteam.server.notice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeRecommend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class NoticeRecommendReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private NoticeRecommendReadService noticeRecommendReadService;

    private Member admin;
    private Notice notice;
    private UUID publicId;

    @Transactional
    @BeforeEach
    void setUp() {
        admin = createAdmin(0);
        publicId = admin.getPublicId();

        notice = createNotice(admin, "공지 제목", "공지", null);
    }

    @Test
    @DisplayName("추천 기록이 없으면 isAlreadyRecommended는 예외 발생")
    void isAlreadyRecommended_예외() {
        assertThrows(PlayHiveException.class, () ->
                noticeRecommendReadService.isAlreadyRecommended(notice.getId(), publicId));
    }

    @Test
    @DisplayName("추천 기록이 존재하면 isAlreadyRecommended는 true 반환")
    void isAlreadyRecommended_정상() {
        noticeRecommendRepository.save(
                NoticeRecommend.builder()
                        .notice(notice)
                        .member(admin)
                        .build()
        );

        boolean result = noticeRecommendReadService.isAlreadyRecommended(notice.getId(), publicId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("추천 기록이 존재하면 isRecommended는 true 반환")
    void isRecommended_true() {
        noticeRecommendRepository.save(
                NoticeRecommend.builder()
                        .notice(notice)
                        .member(admin)
                        .build()
        );

        boolean result = noticeRecommendReadService.isRecommended(notice.getId(), publicId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("추천 기록이 없으면 isRecommended는 false 반환")
    void isRecommended_false() {
        boolean result = noticeRecommendReadService.isRecommended(notice.getId(), publicId);
        assertThat(result).isFalse();
    }
}