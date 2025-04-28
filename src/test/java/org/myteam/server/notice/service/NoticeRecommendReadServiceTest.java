package org.myteam.server.notice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.request.BoardServiceRequest;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.exception.PlayHiveJwtException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.mypage.dto.request.MyBoardServiceRequest;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeRecommend;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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