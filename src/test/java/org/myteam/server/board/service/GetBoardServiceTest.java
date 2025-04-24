package org.myteam.server.board.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class GetBoardServiceTest extends IntegrationTestSupport {

    @Autowired
    private BoardService boardService;
    private Member member;
    private UUID publicId;
    private Board board;

    @BeforeEach
    void setUp() {
        member = createMember(0);
        publicId = member.getPublicId();
        board = createBoard(member, Category.BASEBALL, CategoryType.FREE,
                "title", "content");
    }


    @Test
    @DisplayName("케이스 1: 로그인하지 않은 사용자 → isRecommended == false")
    void getBoard_not_logged_in() {
        // given
        when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.BOARD, board.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);

        // when
        BoardResponse response = boardService.getBoard(board.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.isRecommended()).isFalse();
    }

    @Test
    @DisplayName("케이스 2: 로그인한 사용자 + 추천 이력 있음 → isRecommended == true")
    void getBoard_logged_in_recommended_true() {
        // given
        when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.BOARD, board.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(member.getPublicId());

        // when
        boardRecommendRepository.save(BoardRecommend.builder()
                .board(board)
                .member(member)
                .build());
        BoardResponse response = boardService.getBoard(board.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.isRecommended()).isTrue();
    }

    @Test
    @DisplayName("케이스 3: 로그인한 사용자 + 추천 이력 없음 → isRecommended == false")
    void getBoard_logged_in_recommended_false() {
        // given
        when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.BOARD, board.getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(member.getPublicId());

        // when
        BoardResponse response = boardService.getBoard(board.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.isRecommended()).isFalse();
    }
}
