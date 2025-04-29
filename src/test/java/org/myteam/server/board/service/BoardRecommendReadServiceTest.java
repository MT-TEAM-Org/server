package org.myteam.server.board.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BoardRecommendReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private BoardRecommendReadService boardRecommendReadService;

    private Member member;
    private Board board;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        member = createMember(0);
        publicId = member.getPublicId();

        board = createBoard(member, Category.BASEBALL, CategoryType.FREE,
                "title", "content");
    }

    @Test
    @DisplayName("추천 기록이 없으면 isAlreadyRecommended는 예외 발생")
    void isAlreadyRecommended_예외() {
        assertThrows(PlayHiveException.class, () ->
                boardRecommendReadService.isAlreadyRecommended(board.getId(), publicId));
    }

    @Test
    @DisplayName("추천 기록이 존재하면 isAlreadyRecommended는 true 반환")
    void isAlreadyRecommended_정상() {
        boardRecommendRepository.save(
                BoardRecommend.builder()
                        .board(board)
                        .member(member)
                        .build()
        );

        boolean result = boardRecommendReadService.isAlreadyRecommended(board.getId(), publicId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("추천 기록이 존재하면 isRecommended는 true 반환")
    void isRecommended_true() {
        boardRecommendRepository.save(
                BoardRecommend.builder()
                        .board(board)
                        .member(member)
                        .build()
        );

        boolean result = boardRecommendReadService.isRecommended(board.getId(), publicId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("추천 기록이 없으면 isRecommended는 false 반환")
    void isRecommended_false() {
        boolean result = boardRecommendReadService.isRecommended(board.getId(), publicId);
        assertThat(result).isFalse();
    }
}