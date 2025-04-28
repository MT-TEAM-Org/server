package org.myteam.server.mypage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.comment.service.CommentReadService;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.page.response.PageableCustomResponse;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.mypage.dto.request.MyBoardServiceRequest;
import org.myteam.server.mypage.dto.request.MyPageRequest.*;
import org.myteam.server.mypage.dto.response.MyPageResponse.*;
import org.myteam.server.notice.dto.response.NoticeResponse;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;


class MyPageReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MyPageReadService myPageReadService;

    private Member member;
    private List<Board> boardList = new ArrayList<>();

    @Transactional
    @BeforeEach
    void setUp() {
        member = createMember(1);
        for (int i = 1; i <= 10; i++) {
            boardList.add(createBoard(member, Category.BASEBALL, CategoryType.FREE,
                    "title" + i, "content" + i));
        }
    }

    @Test
    @DisplayName("1. 회원 통계 정보 조회 성공")
    void getMemberInfo_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        // when
        MemberStatsResponse response = myPageReadService.getMemberInfo();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCreatedPostCount()).isEqualTo(10);
        assertThat(response.getCreatedCommentCount()).isEqualTo(0);
        assertThat(response.getCreatedInquiryCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("2. 회원 수정용 전체 정보 조회 성공")
    void getMemberAllInfo_success() {
        // when
        MemberModifyResponse response = myPageReadService.getMemberAllInfo();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(member.getEmail());
        assertThat(response.getNickname()).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("3. 내가 쓴 게시글 조회 성공")
    void getMemberPosts_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);
        for (int i = 0; i < 10; i++) {
            when(redisCountService.getCommonCount(
                    eq(ServiceType.CHECK),
                    eq(DomainType.BOARD),
                    eq(boardList.get(i).getId()),
                    isNull()
            )).thenReturn(new CommonCountDto(0, 0, 0));
        }
        MyBoardServiceRequest request = MyBoardServiceRequest.builder()
                .page(1)
                .size(10)
                .build();

        // when
        BoardListResponse response = myPageReadService.getMemberPosts(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList().getContent()).hasSize(10);
        assertThat(response.getList().getContent())
                .extracting("title")
                .containsExactlyInAnyOrder(
                        "title1", "title2", "title3", "title4", "title5",
                        "title6", "title7", "title8", "title9", "title10"
                );

    }
}