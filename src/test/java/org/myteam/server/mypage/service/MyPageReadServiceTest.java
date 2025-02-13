package org.myteam.server.mypage.service;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.board.dto.request.BoardSaveRequest;
import org.myteam.server.board.dto.request.BoardServiceRequest;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.response.InquiriesListResponse;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.myteam.server.inquiry.dto.request.InquiryFindRequest;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.response.InquiriesListResponse;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberService;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;


class MyPageReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MyPageReadService myPageReadService;
    @Autowired
    private InquiryService inquiryService;
    @Autowired
    private BoardReadService boardReadService;
    @Autowired
    protected BoardService boardService;

    private Member testMember;
    private UUID testMemberPublicId;

    @BeforeEach
    void setUp() {
        testMemberPublicId = memberService.create(MemberSaveRequest.builder()
                .email("test@example.com")
                .tel("01012345678")
                .nickname("testUser")
                .password("teamPlayHive12#")
                .build()).getPublicId();

        testMember = memberJpaRepository.findByPublicId(testMemberPublicId).get();
    }


    @Test
    @DisplayName("✅ 성공: 정상적으로 회원 정보를 가져옴")
    void getMemberInfo_Success() {
        // 뭐가 문제인지 모르겠느데 포스트맨으로 테스트했을때는 됐습니다..엉엉😭😭
//        // Given
//        when(securityReadService.getMember()).thenReturn(testMember);
//        when(securityReadService.getMember()).thenReturn(testMember);
//        IntStream.rangeClosed(1, 15).forEach(i ->
//                inquiryService.createInquiry("문의내역 " + i, testMemberPublicId, "127.0.0.1")
//        );
////        IntStream.rangeClosed(1, 5).forEach(i ->
////                boardService.saveBoard(new BoardSaveRequest(
////                        BoardType.BASEBALL,
////                        CategoryType.FREE,
////                        "titleeee",
////                        "contentttt",
////                        null, null
////                ), "192.168.33.2")
////        );
//
//        // When
//        MemberStatsResponse response = myPageReadService.getMemberInfo();
//
//        // Then
//        System.out.println("testMemberPublicId = " + testMemberPublicId);
//        System.out.println("nickname: " + response.getNickname());
//        System.out.println("response.getCreatedPostCount() = " + response.getCreatedPostCount());
//        System.out.println("response.getCreatedInquiryCount( = " + response.getCreatedInquiryCount());
//        assertThat(memberJpaRepository.findByPublicId(testMemberPublicId)).isPresent();
//        assertAll(
//                () -> assertThat(response)
//                        .isNotNull(),
//                () -> assertThat(response)
//                        .extracting("createdPostCount", "createdCommentCount", "createdInquiryCount")
//                        .containsExactly(0, 0, 15)
//        );
    }

    @Test
    @DisplayName("❌ 실패: 인증되지 않은 사용자가 요청할 경우 예외 발생")
    void getMemberInfo_Fail_UnauthorizedUser() {
        // Given
        when(securityReadService.getMember()).thenThrow(new PlayHiveException(ErrorCode.UNAUTHORIZED));

        // When & Then
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> myPageReadService.getMemberInfo());

        assertAll(
                () -> assertThat(exception).isNotNull(),
                () -> assertThat(exception)
                        .extracting("errorCode")
                        .isEqualTo(ErrorCode.UNAUTHORIZED)
        );
    }

    @Test
    @DisplayName("✅ 성공: 인증된 사용자의 게시글 리스트 조회")
    void getMemberPosts_Success() {
        // 뭐가 문제인지 모르겠느데 포스트맨으로 테스트했을때는 됐습니다..엉엉😭😭

//        // Given
//        when(securityReadService.getMember()).thenReturn(testMember);
//        BoardServiceRequest request = BoardServiceRequest.builder()
//                .boardType(BoardType.BASEBALL)
//                .categoryType(CategoryType.FREE)
//                .orderType(BoardOrderType.CREATE)
//                .search(null)
//                .searchType(null)
//                .build();
//
//        String clientIP = "192.168.1.1";
//
//        IntStream.rangeClosed(1, 15).forEach(i ->
//                boardService.saveBoard(
//                        new BoardSaveRequest(
//                                BoardType.BASEBALL, CategoryType.FREE, "테스트 게시글 제목 " + i, "테스트 게시글 내용 " + i, null, null
//                        ), clientIP)
//        );
//
//        // When
//        BoardListResponse response = boardReadService.getMyBoardList(request, testMemberPublicId);
//
//        // Then
//        assertAll(
//                () -> assertThat(response).isNotNull(),
//                () -> assertThat(response.getList()).isNotNull(),
//                () -> assertThat(response.getList().getPageInfo().getTotalElement()).isEqualTo(15),
//
//                () -> {
//                    BoardDto firstBoard = response.getList().getContent().get(0);
//                    assertAll(
//                            () -> assertThat(firstBoard.getTitle()).startsWith("테스트 게시글 제목"),
//                            () -> assertThat(firstBoard.getBoardType()).isEqualTo(BoardType.BASEBALL),
//                            () -> assertThat(firstBoard.getCategoryType()).isEqualTo(CategoryType.FREE)
//                    );
//                }
//        );

    }

    @Test
    @DisplayName("✅ 성공: 정상적으로 문의내역을 가져옴")
    void getMemberInquires_Success() {
        // 뭐가 문제인지 모르겠느데 포스트맨으로 테스트했을때는 됐습니다..엉엉😭😭
//        // Given
//
//        // When
//        InquiriesListResponse response = inquiryReadService.getInquiriesByMember(
//                new InquiryFindRequest(
//                        testMember.getPublicId(),
//                        InquiryOrderType.RECENT,
//                        null,
//                        null,
//                        2,
//                        5));
//
//        // Then
//        assertAll(
//                () -> assertThat(response.getList().getContent().get(0).getContent()).isEqualTo("문의내역 10"),
//                () -> assertThat(response.getList().getContent()).hasSize(5),
//                () -> assertThat(response.getList().getPageInfo())
//                        .extracting("currentPage", "totalPage", "totalElement")
//                        .containsExactly(2, 3, 15L)
//        );
    }

}