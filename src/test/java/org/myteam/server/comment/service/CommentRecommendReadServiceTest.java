package org.myteam.server.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentRecommend;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest.*;
import org.myteam.server.comment.dto.response.CommentResponse.*;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.mypage.dto.request.MyCommentServiceRequest;
import org.myteam.server.mypage.dto.response.MyCommentListResponse;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.myteam.server.global.exception.ErrorCode.COMMENT_NOT_FOUND;
import static org.myteam.server.global.exception.ErrorCode.USER_NOT_FOUND;

class CommentRecommendReadServiceTest extends IntegrationTestSupport {

    @Autowired private CommentRecommendReadService commentRecommendReadService;
    private Member member;
    private UUID publicId;
    private List<Board> boardList = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();
    private List<Comment> replyList = new ArrayList<>();
    private final Long NON_EXIST_ID = 999999L;

    @BeforeEach
    void setUp() {
        member = createMember(0);
        publicId = member.getPublicId();
        for (int i = 1; i <= 5; i++) {
            boardList.add(createBoard(member, Category.BASEBALL, CategoryType.FREE,
                    "title" + i, "content" + i));
        }

        for (int i = 1; i <= 4; i++) {
            Comment comment = commentFactory.createComment(
                    CommentType.BOARD,
                    boardList.get(i).getId(),
                    member,
                    null,
                    "comment" + i,
                    null,
                    "127.0.0.1",
                    null
            );
            commentRepository.save(comment);
            commentList.add(comment);
        }

        for (int i = 1; i <= 3; i++) {
            Comment reply = commentFactory.createComment(
                    CommentType.BOARD,
                    boardList.get(i).getId(),
                    member,
                    null,
                    "reply" + i,
                    null,
                    "127.0.0.1",
                    commentList.get(i - 1).getId()
            );
            commentRepository.save(reply);
            replyList.add(reply);
        }
    }

    @Test
    @DisplayName("confirmExistRecommend - 추천하지 않았으면 예외 없음")
    void confirmExistRecommend_notYet() {
        assertThatCode(() ->
                commentRecommendReadService.confirmExistRecommend(commentList.get(0).getId(), publicId)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("confirmExistRecommend - 이미 추천했다면 예외 발생")
    void confirmExistRecommend_alreadyExists() {
        commentRecommendRepository.save(CommentRecommend.builder()
                .comment(commentList.get(0))
                .member(member)
                .build());

        assertThatThrownBy(() ->
                commentRecommendReadService.confirmExistRecommend(commentList.get(0).getId(), publicId)
        ).isInstanceOf(PlayHiveException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ALREADY_MEMBER_RECOMMEND_COMMENT);
    }

    @Test
    @DisplayName("isAlreadyRecommended - 추천한 적 있으면 true 반환")
    void isAlreadyRecommended_true() {
        commentRecommendRepository.save(CommentRecommend.builder()
                .comment(commentList.get(0))
                .member(member)
                .build());

        boolean result = commentRecommendReadService.isAlreadyRecommended(commentList.get(0).getId(), publicId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAlreadyRecommended - 추천하지 않았으면 예외 발생")
    void isAlreadyRecommended_false_throws() {
        assertThatThrownBy(() ->
                commentRecommendReadService.isAlreadyRecommended(commentList.get(1).getId(), publicId)
        ).isInstanceOf(PlayHiveException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
    }

    @Test
    @DisplayName("isRecommended - 추천했을 경우 true 반환")
    void isRecommended_true() {
        commentRecommendRepository.save(CommentRecommend.builder()
                .comment(commentList.get(0))
                .member(member)
                .build());

        boolean result = commentRecommendReadService.isRecommended(commentList.get(0).getId(), publicId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isRecommended - 추천하지 않았을 경우 false 반환")
    void isRecommended_false() {
        boolean result = commentRecommendReadService.isRecommended(commentList.get(1).getId(), publicId);
        assertThat(result).isFalse();
    }
}