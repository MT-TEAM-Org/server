package org.myteam.server.board.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.request.BoardServiceRequest;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.mypage.dto.request.MyBoardServiceRequest;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.myteam.server.global.exception.ErrorCode.BOARD_NOT_FOUND;


class BoardReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private BoardReadService boardReadService;

    private UUID publicId;
    private List<Board> boardList = new ArrayList<>();
    private Member member;

    @BeforeEach
    public void setUp() {
        member = createMember(1);
        publicId = member.getPublicId();
        for (int i = 1; i <= 5; i++) {
            boardList.add(createBoard(member, Category.BASEBALL, CategoryType.FREE,
                    "title" + i, "content" + i));
        }
    }

    @Test
    @DisplayName("게시글 ID로 조회 성공")
    void findById_존재하는_게시글() {
        // given
        Board board = createBoard(member, Category.BASEBALL, CategoryType.FREE,
                "title", "content");

        // when
        Board result = boardReadService.findById(board.getId());

        // then
        assertThat(result.getId()).isEqualTo(board.getId());
    }

    @Test
    @DisplayName("게시글 ID로 조회 실패 - 예외 발생")
    void findById_없는_게시글() {
        // given && when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            boardReadService.findById(100L);
        });

        // then
        assertEquals(BOARD_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시글 리스트 조회")
    void getBoardList_정상조회() {
        // given
        for (int i = 0; i < boardList.size(); i++) {
            when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.BOARD, boardList.get(i).getId(), null))
                    .thenReturn(new CommonCountDto(0, 0, 0));
        }
        BoardServiceRequest request = BoardServiceRequest.builder()
                .boardType(Category.BASEBALL)
                .categoryType(CategoryType.FREE)
                .orderType(null)
                .searchType(null)
                .search(null)
                .page(1)
                .size(10)
                .build();

        // when
        BoardListResponse response = boardReadService.getBoardList(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList().getContent()).hasSize(5);
        assertThat(response.getList().getContent())
                .extracting("title")
                .containsExactlyInAnyOrder("title1", "title2", "title3", "title4", "title5");
    }

    @Test
    @DisplayName("내 게시글 리스트 조회 - 게시글 5개 조회됨")
    void getMyBoardList_정상조회() {
        // given
        for (int i = 0; i < boardList.size(); i++) {
            when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.BOARD, boardList.get(i).getId(), null))
                    .thenReturn(new CommonCountDto(0, 0, 0));
        }
        MyBoardServiceRequest request = MyBoardServiceRequest.builder()
                .orderType(null)
                .searchType(null)
                .search(null)
                .page(1)
                .size(10)
                .build();

        // when
        BoardListResponse response = boardReadService.getMyBoardList(request, publicId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList().getContent()).hasSize(5);
        assertThat(response.getList().getContent())
                .extracting("title")
                .containsExactlyInAnyOrder("title1", "title2", "title3", "title4", "title5");
    }

    @Test
    @DisplayName("내 게시글 개수 조회")
    void getMyBoardListCount_조회() {
        // given && when
        int count = boardReadService.getMyBoardListCount(publicId);

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("게시글 존재 여부 확인 - true")
    void existsById_true() {
        // given && when
        boolean exists = boardReadService.existsById(boardList.get(0).getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("게시글 존재 여부 확인 - false")
    void existsById_false() {
        // given && when
        boolean exists = boardReadService.existsById(100L);

        // then
        assertThat(exists).isFalse();
    }
}