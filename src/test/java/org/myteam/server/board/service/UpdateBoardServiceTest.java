package org.myteam.server.board.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.board.dto.request.BoardRequest;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.myteam.server.global.exception.ErrorCode.INVALID_TYPE;
import static org.myteam.server.global.exception.ErrorCode.POST_AUTHOR_MISMATCH;

public class UpdateBoardServiceTest extends IntegrationTestSupport {

    @Autowired
    private BoardService boardService;
    @MockBean
    private BoardRecommendReadService boardRecommendReadService;
    private Member author;
    private Member other;
    private Member admin;
    private UUID publicId;
    private Board board;

    @BeforeEach
    void setUp() {
        author = createMember(0);
        other = createMember(1);
        admin = createAdmin(2);
        publicId = author.getPublicId();
        board = createBoard(author, Category.BASEBALL, CategoryType.FREE,
                "title", "content");
    }

    @Transactional
    @Test
    @DisplayName("케이스 1: 작성자가 추천까지 한 경우 → 추천 true")
    void updateBoard_author_recommend_true() {
        // given
        Member boardAuthor = boardRepository.findById(board.getId()).orElseThrow().getMember();

        when(securityReadService.getMember()).thenReturn(boardAuthor);
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(boardAuthor.getPublicId());
        when(memberReadService.findById(boardAuthor.getPublicId())).thenReturn(boardAuthor);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD, board.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(boardRecommendReadService.isRecommended(board.getId(), boardAuthor.getPublicId()))
                .thenReturn(true);

        // when
        BoardRequest request = new BoardRequest(Category.ESPORTS, CategoryType.FREE, "new title", "new content", null, null);
        BoardResponse response = boardService.updateBoard(request, board.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("new title");
        assertThat(response.isRecommended()).isTrue();
    }

    @Transactional
    @Test
    @DisplayName("케이스 2: 작성자지만 추천 안 한 경우 → 추천 false")
    void updateBoard_author_recommend_false() {
        // given
        Member boardAuthor = boardRepository.findById(board.getId()).orElseThrow().getMember();

        when(securityReadService.getMember()).thenReturn(boardAuthor);
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(boardAuthor.getPublicId());
        when(memberReadService.findById(boardAuthor.getPublicId())).thenReturn(boardAuthor);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD, board.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(boardRecommendReadService.isRecommended(board.getId(), boardAuthor.getPublicId()))
                .thenReturn(false);

        // when
        BoardRequest request = new BoardRequest(Category.ESPORTS, CategoryType.FREE, "new title", "new content", null, null);
        BoardResponse response = boardService.updateBoard(request, board.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("new title");
        assertThat(response.isRecommended()).isFalse();
    }

    @Test
    @DisplayName("케이스 3: isEsports가 false → confirmEsports() 호출되며 예외 발생 가능")
    void updateBoard_categoryNotEsports_shouldCallConfirm() {
        // given
        when(securityReadService.getMember()).thenReturn(author);
        when(memberReadService.findById(publicId)).thenReturn(author);
        when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.BOARD, 1L, null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        BoardRequest request = new BoardRequest(Category.BASEBALL, CategoryType.VERIFICATION,
                "테스트 제목", "테스트 내용", "http://example.com", "http://example.com/thumb.png");

        // when
        PlayHiveException exception = Assertions.assertThrows(PlayHiveException.class, () -> {
            boardService.saveBoard(request, "127.0.0.1");
        });

        // then
        Assertions.assertEquals(INVALID_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("케이스 4: 관리자가 수정 요청 → 정상 수정됨 / 추천 True")
    void updateBoard_admin_canModify_recommend() {
        // given
        when(securityReadService.getMember()).thenReturn(admin);
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(admin.getPublicId());
        when(memberReadService.findById(admin.getPublicId())).thenReturn(admin);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD, board.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(boardRecommendReadService.isRecommended(board.getId(), admin.getPublicId()))
                .thenReturn(true);

        // when
        BoardRequest request = new BoardRequest(Category.ESPORTS, CategoryType.FREE, "new title", "new content", null, null);
        BoardResponse response = boardService.updateBoard(request, board.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("new title");
        assertThat(response.isRecommended()).isTrue();
    }

    @Test
    @DisplayName("케이스 5: 관리자가 수정 요청 → 정상 수정됨 / 추천 false")
    void updateBoard_admin_canModify_not_recommend() {
        // given
        when(securityReadService.getMember()).thenReturn(admin);
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(admin.getPublicId());
        when(memberReadService.findById(admin.getPublicId())).thenReturn(admin);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD, board.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(boardRecommendReadService.isRecommended(board.getId(), admin.getPublicId()))
                .thenReturn(false);

        // when
        BoardRequest request = new BoardRequest(Category.ESPORTS, CategoryType.FREE, "new title", "new content", null, null);
        BoardResponse response = boardService.updateBoard(request, board.getId());

        // then
        assertThat(response.getTitle()).isEqualTo("new title");
        assertThat(response.isRecommended()).isFalse();
    }

    @Test
    @DisplayName("케이스 6: 작성자도 아니고 관리자도 아닌 경우 → 예외 발생")
    void updateBoard_notAuthorOrAdmin_shouldThrow() {
        // given
        when(securityReadService.getMember()).thenReturn(other);
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(other.getPublicId());
        when(memberReadService.findById(other.getPublicId())).thenReturn(other);
        when(redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD, board.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(boardRecommendReadService.isRecommended(board.getId(), other.getPublicId()))
                .thenReturn(false);

        BoardRequest request = new BoardRequest(Category.ESPORTS, CategoryType.FREE, "bad", "no", null, null);

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            boardService.updateBoard(request, board.getId());
        });

        // then
        assertEquals(POST_AUTHOR_MISMATCH, exception.getErrorCode());
    }
}
