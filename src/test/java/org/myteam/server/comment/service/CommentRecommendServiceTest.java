package org.myteam.server.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentRecommend;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.myteam.server.global.exception.ErrorCode.*;

class CommentRecommendServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentRecommendService commentRecommendService;
    @Autowired
    private CommentRecommendReadService commentRecommendReadService;
    private Member member;
    private UUID publicId;
    private List<Board> boardList = new ArrayList<>();
    private Comment comment;
    private final Long NON_EXIST_ID = 999999L;

    @BeforeEach
    void setUp() {
        member = createMember(0);
        publicId = member.getPublicId();
        for (int i = 1; i <= 5; i++) {
            boardList.add(createBoard(member, Category.BASEBALL, CategoryType.FREE,
                    "title" + i, "content" + i));
        }

        comment = commentFactory.createComment(
                CommentType.BOARD,
                boardList.get(0).getId(),
                member,
                null,
                "comment",
                null,
                "127.0.0.1",
                null
        );
        commentRepository.save(comment);
    }

    @Test
    @DisplayName("댓글 추천 - 처음 추천하면 정상 저장")
    @Transactional
    void recommendComment_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        // when
        commentRecommendService.recommendComment(comment.getId());

        // then
        assertThat(commentRecommendReadService.isAlreadyRecommended(comment.getId(), member.getPublicId()))
                .isTrue();
        assertThat(comment.getRecommendCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 추천 - 이미 추천한 경우 예외 발생")
    void recommendComment_alreadyRecommended() {
        // given
        when(securityReadService.getMember()).thenReturn(member);
        commentRecommendService.recommendComment(comment.getId());

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            commentRecommendService.recommendComment(comment.getId());
        });

        // then
        assertEquals(ALREADY_MEMBER_RECOMMEND_COMMENT, exception.getErrorCode());
    }

    @Transactional
    @Test
    @DisplayName("댓글 추천 취소 - 정상 취소")
    void cancelRecommendComment_success() {
        // given
        commentRecommendService.recommendComment(comment.getId());
        when(securityReadService.getMember()).thenReturn(member);

        // when
        commentRecommendService.cancelRecommendComment(comment.getId());

        // then
        assertThrows(PlayHiveException.class, () -> {
            commentRecommendReadService.isAlreadyRecommended(comment.getId(), member.getPublicId());
        });
        assertThat(comment.getRecommendCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 추천 취소 - 추천하지 않았으면 예외 발생")
    void cancelRecommendComment_notRecommended() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            commentRecommendService.cancelRecommendComment(comment.getId());
        });

        // then
        assertEquals(NO_MEMBER_RECOMMEND_RECORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 ID 기준 추천 모두 삭제")
    void deleteCommentRecommendByPost_success() {
        // given
        commentRecommendRepository.save(CommentRecommend.createCommentRecommend(comment, member));

        // when
        commentRecommendService.deleteCommentRecommendByPost(comment.getId());

        // then
        assertThrows(PlayHiveException.class, () -> {
            commentRecommendReadService.isAlreadyRecommended(comment.getId(), member.getPublicId());
        });
        assertThat(comment.getRecommendCount()).isEqualTo(0);
    }
}