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
import org.myteam.server.global.exception.ErrorCode;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.myteam.server.global.exception.ErrorCode.INVALID_TYPE;
import static org.myteam.server.global.exception.ErrorCode.POST_AUTHOR_MISMATCH;

import java.util.UUID;

public class DeleteBoardServiceTest extends IntegrationTestSupport {

    @Autowired
    private BoardService boardService;
    @MockBean
    protected MemberReadService memberReadService;
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
    @DisplayName("케이스 1: 작성자가 삭제 요청 → 성공")
    void deleteBoard_by_author_success() {
        // given
        Member boardAuthor = boardRepository.findById(board.getId()).orElseThrow().getMember();
        when(securityReadService.getMember()).thenReturn(boardAuthor);
        when(memberReadService.findById(author.getPublicId())).thenReturn(boardAuthor);

        // when && then
        assertDoesNotThrow(() -> boardService.deleteBoard(board.getId()));
    }

    @Test
    @DisplayName("케이스 2: 관리자가 삭제 요청 → 성공")
    void deleteBoard_by_admin_success() {
        // given
        when(securityReadService.getMember()).thenReturn(admin);
        when(memberReadService.findById(admin.getPublicId())).thenReturn(admin);

        // when && then
        assertDoesNotThrow(() -> boardService.deleteBoard(board.getId()));
    }

    @Test
    @DisplayName("케이스 3: 작성자도 관리자도 아닌 유저가 삭제 요청 → 예외 발생")
    void deleteBoard_by_outsider_throws() {
        // given
        when(securityReadService.getMember()).thenReturn(other);
        when(memberReadService.findById(other.getPublicId())).thenReturn(other);

        // when
        PlayHiveException ex = assertThrows(PlayHiveException.class, () ->
                boardService.deleteBoard(board.getId()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_AUTHOR_MISMATCH);
    }
}
