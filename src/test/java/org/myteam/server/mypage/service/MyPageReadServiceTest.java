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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;


class MyPageReadServiceTest extends IntegrationTestSupport {

    private Member testMember;
    private Member otherMember;

    private UUID testMemberPublicId;
    private UUID otherMemberPublicId;

    @BeforeEach
    @Transactional
    void setUp() {
        testMemberPublicId = memberService.create(MemberSaveRequest.builder()
                .email("test@example.com")
                .tel("01012345678")
                .nickname("testUser")
                .password("teamPlayHive12#")
                .build()).getPublicId();
        otherMemberPublicId = memberService.create(MemberSaveRequest.builder()
                .email("other@example.com")
                .tel("01087654321")
                .nickname("otherUser")
                .password("otherMember!@#")
                .build()).getPublicId();

        testMember = memberJpaRepository.findByPublicId(testMemberPublicId).get();
        IntStream.rangeClosed(1, 15).forEach(i ->
                inquiryService.createInquiry("문의내역 " + i, testMemberPublicId, "127.0.0.1")
        );
        otherMember = memberJpaRepository.findByPublicId(otherMemberPublicId).get();
        IntStream.rangeClosed(1, 15).forEach(i ->
                inquiryService.createInquiry("건의사항 " + i, otherMemberPublicId, "127.0.0.1")
        );
    }


    @Test
    @DisplayName("✅ 성공: 정상적으로 회원 정보를 가져옴")
    void getMemberInfo_Success() {
        // Given
        int expectedPostCount = 5;
        int expectedCommentCount = 0;
        int expectedInquiryCount = 3;

        when(securityReadService.getMember()).thenReturn(testMember);
        when(boardReadService.getMyBoardListCount(testMemberPublicId)).thenReturn(expectedPostCount);
        when(inquiryReadService.getInquiriesCountByMember(testMemberPublicId)).thenReturn(expectedInquiryCount);

        // When
        MemberStatsResponse response = myPageReadService.getMemberInfo();

        // Then
        assertAll(
                () -> assertThat(response)
                        .isNotNull(),
                () -> assertThat(response)
                        .extracting("createdPostCount", "createdCommentCount", "createdInquiryCount")
                        .containsExactly(expectedPostCount, expectedCommentCount, expectedInquiryCount)
        );
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
        // Given
        BoardServiceRequest request = BoardServiceRequest.builder()
                .boardType(BoardType.BASEBALL)
                .categoryType(CategoryType.FREE)
                .orderType(BoardOrderType.CREATE)
                .search(null)
                .searchType(null)
                .build();

        String clientIP = "192.168.1.1";

        IntStream.rangeClosed(1, 15).forEach(i ->
                boardService.saveBoard(
                        new BoardSaveRequest(
                                BoardType.BASEBALL, CategoryType.FREE, "테스트 게시글 제목 " + i, "테스트 게시글 내용 " + i, null, null
                        ), clientIP)
        );

        List<BoardDto> boardList = IntStream.rangeClosed(1, 15)
                .mapToObj(i -> new BoardDto(
                        BoardType.BASEBALL,  // boardType
                        CategoryType.FREE,   // categoryType
                        (long) i,            // id
                        "테스트 게시글 제목 " + i, // title
                        "192.168.1.1",       // createdIp
                        "https://example.com/thumbnail" + i, // thumbnail
                        UUID.randomUUID(),   // publicId (랜덤 UUID)
                        "nickname" + i,      // nickname
                        i,                   // commentCount
                        LocalDateTime.now().minusDays(i), // createdAt (생성일을 i일 전으로 설정)
                        LocalDateTime.now()  // updatedAt (현재 시간)
                ))
                .toList();

        Page<BoardDto> boardPage = new PageImpl<>(boardList, PageRequest.of(0, 15), boardList.size());

        // When
        BoardListResponse mockResponse = BoardListResponse.createResponse(PageCustomResponse.of(boardPage));

        when(securityReadService.getMember()).thenReturn(testMember);
        when(boardReadService.getMyBoardList(request, testMemberPublicId)).thenReturn(mockResponse);

        // Then
        assertAll(
                () -> assertThat(mockResponse).isNotNull(),
                () -> assertThat(mockResponse.getList()).isNotNull(),
                () -> assertThat(mockResponse.getList().getPageInfo().getTotalElement()).isEqualTo(15),

                () -> {
                    BoardDto firstBoard = mockResponse.getList().getContent().get(0);
                    assertAll(
                            () -> assertThat(firstBoard.getTitle()).startsWith("테스트 게시글 제목"),
                            () -> assertThat(firstBoard.getBoardType()).isEqualTo(BoardType.BASEBALL),
                            () -> assertThat(firstBoard.getCategoryType()).isEqualTo(CategoryType.FREE)
                    );
                }
        );

    }

    @Test
    @DisplayName("✅ 성공: 정상적으로 문의내역을 가져옴")
    void getMemberInquires_Success() {
        // Given

        // When
        InquiriesListResponse response = inquiryReadService.getInquiriesByMember(
                new InquirySearchRequest(
                        testMember.getPublicId(),
                        InquiryOrderType.RECENT,
                        null,
                        null,
                        2,
                        5));

        // Then
        assertAll(
                () -> assertThat(response.getList().getContent().get(0).getContent()).isEqualTo("문의내역 10"),
                () -> assertThat(response.getList().getContent()).hasSize(5),
                () -> assertThat(response.getList().getPageInfo())
                        .extracting("currentPage", "totalPage", "totalElement")
                        .containsExactly(2, 3, 15L)
        );
    }

}