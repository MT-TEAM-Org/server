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
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.myteam.server.global.exception.ErrorCode.COMMENT_NOT_FOUND;
import static org.myteam.server.global.exception.ErrorCode.USER_NOT_FOUND;

class CommentReadServiceTest extends IntegrationTestSupport {

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
    @DisplayName("댓글이 존재하면 정상적으로 반환된다")
    void findById_success() {
        // given
        Comment comment = commentFactory.createComment(
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

        // when
        Comment result = commentReadService.findById(comment.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getComment()).isEqualTo("comment");
    }

    @Test
    @DisplayName("댓글이 존재하지 않으면 COMMENT_NOT_FOUND 예외가 발생한다")
    void findById_not_found() {
        // given

        // when & then
        PlayHiveException ex = assertThrows(PlayHiveException.class, () -> {
            commentReadService.findById(NON_EXIST_ID);
        });

        assertThat(ex.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글과 타입이 일치하면 정상적으로 반환된다")
    void findByIdAndType_success() {
        // given
        Comment comment = commentFactory.createComment(
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

        // when
        Comment result = commentReadService.findByIdAndCommentType(comment.getId(), CommentType.BOARD);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getComment()).isEqualTo("comment");
    }

    @Test
    @DisplayName("댓글과 타입이 일치하지 않으면 COMMENT_NOT_FOUND 예외가 발생한다")
    void findByIdAndType_not_found() {
        // given
        Comment comment = commentFactory.createComment(
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

        // when & then
        PlayHiveException ex = assertThrows(PlayHiveException.class, () -> {
            Comment result = commentReadService.findByIdAndCommentType(comment.getId(), CommentType.NEWS);
        });

        assertThat(ex.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인한 사용자가 댓글 목록 요청 시 정상 응답")
    void getComments_loggedIn() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);
        CommentListRequest request = CommentListRequest.builder()
                .type(CommentType.BOARD)
                .build();

        // when
        CommentSaveListResponse response = commentReadService.getComments(
                boardList.get(2).getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent().getContent()).hasSize(1);
        assertThat(response.getContent().getContent().get(0).getComment()).isEqualTo("comment2");
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 댓글 목록 요청 시 정상 응답")
    void getComments_notLoggedIn() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);
        CommentListRequest request = CommentListRequest.builder()
                .type(CommentType.BOARD)
                .build();

        // when
        CommentSaveListResponse response = commentReadService.getComments(
                boardList.get(2).getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent().getContent()).hasSize(1);
        assertThat(response.getContent().getContent().get(0).getComment()).isEqualTo("comment2");
    }

    @Test
    @DisplayName("로그인한 사용자가 댓글 상세 조회 호출, 댓글 존재")
    void getCommentDetail_loggedIn() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);

        // when
        CommentSaveResponse response = commentReadService.getCommentDetail(commentList.get(0).getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("comment1");
    }

    @Test
    @DisplayName("로그인한 사용자가 댓글 상세 조회 호출, 댓글 존재하지 않음")
    void getCommentDetail_loggedIn_not_comment() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);

        // when
        PlayHiveException ex = assertThrows(PlayHiveException.class, () -> {
            CommentSaveResponse response = commentReadService.getCommentDetail(NON_EXIST_ID);
        });

        // then
        assertThat(ex.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 댓글 상세 조회 호출, 댓글 존재")
    void getCommentDetail_non_loggedIn() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);

        // when
        CommentSaveResponse response = commentReadService.getCommentDetail(commentList.get(0).getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("comment1");
    }

    @Test
    @DisplayName("로그인하지않은 사용자가 댓글 상세 조회 호출, 댓글 존재하지 않음")
    void getCommentDetail_non_loggedIn_not_comment() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);

        // when
        PlayHiveException ex = assertThrows(PlayHiveException.class, () -> {
            CommentSaveResponse response = commentReadService.getCommentDetail(NON_EXIST_ID);
        });

        // then
        assertThat(ex.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 사용자가 댓글 상세 조회 호출, 대댓글 존재")
    void getCommentDetail_loggedIn_reply() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);

        // when
        CommentSaveResponse response = commentReadService.getCommentDetail(commentList.get(0).getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("comment1");
        assertThat(response.getReplyList()).hasSize(1);
        assertThat(response.getReplyList().get(0).getComment()).isEqualTo("reply1");
    }

    @Test
    @DisplayName("로그인 사용자가 댓글 상세 조회 호출, 대댓글 존재하지 않음")
    void getCommentDetail_loggedIn_not_reply() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);

        // when
        CommentSaveResponse response = commentReadService.getCommentDetail(commentList.get(3).getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("comment4");
        assertThat(response.getReplyList()).hasSize(0);
    }

    @Test
    @DisplayName("케이스 1: 로그인하지 않은 사용자 → 추천 체크 없이 IP 마스킹만 적용")
    void getBestComments_notLoggedIn() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);
        CommentListRequest request = CommentListRequest.builder()
                .type(CommentType.BOARD)
                .build();

        // when
        BestCommentSaveListResponse result = commentReadService.getBestComments(
                        boardList.get(2).getId(), request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().getContent().get(0).getComment()).contains("comment2");
        assertThat(result.getContent().getContent().get(0).getCreatedIp()).contains("*");
        assertThat(result.getContent().getContent().get(0).isRecommended()).isFalse();
    }

    @Test
    @DisplayName("케이스 2: 로그인 + 추천한 댓글 → 추천 true 설정")
    void getBestComments_loggedIn_recommended() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);
        CommentRecommend commentRecommend = CommentRecommend.builder()
                .comment(commentList.get(0))
                .member(member)
                .build();
        commentRecommendRepository.save(commentRecommend);
        CommentListRequest request = CommentListRequest.builder()
                .type(CommentType.BOARD)
                .build();

        // when
        BestCommentSaveListResponse result = commentReadService.getBestComments(
                boardList.get(1).getId(), request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().getContent().get(0).getComment()).contains("comment1");
        assertThat(result.getContent().getContent().get(0).getCreatedIp()).contains("*");
        assertThat(result.getContent().getContent().get(0).isRecommended()).isTrue();
    }

    @Test
    @DisplayName("케이스 3: 로그인 + 추천하지 않은 댓글 → 추천 false 설정")
    void getBestComments_loggedIn_notRecommended() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);
        CommentListRequest request = CommentListRequest.builder()
                .type(CommentType.BOARD)
                .build();

        // when
        BestCommentSaveListResponse result = commentReadService.getBestComments(
                boardList.get(1).getId(), request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().getContent().get(0).getComment()).contains("comment1");
        assertThat(result.getContent().getContent().get(0).getCreatedIp()).contains("*");
        assertThat(result.getContent().getContent().get(0).isRecommended()).isFalse();
    }

    @Test
    @DisplayName("내 댓글 리스트 조회 - 정상 동작")
    void getMyCommentList_success() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(publicId);

        MyCommentServiceRequest request = MyCommentServiceRequest.builder()
                .commentType(CommentType.BOARD)
                .orderType(null)
                .searchType(null)
                .search(null)
                .page(1)
                .size(10)
                .build();

        // when
        MyCommentListResponse response = commentReadService.getMyCommentList(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList().getContent()).hasSize(7);
        assertThat(response.getList().getContent().get(0).getCommentResponse().getComment()).startsWith("reply");
    }

    @Test
    @DisplayName("내 댓글 리스트 조회 - 오류(로그인 안됨)")
    void getMyCommentList_false() {
        // given
        PlayHiveException exception = new PlayHiveException(USER_NOT_FOUND);
        when(securityReadService.getMember()).thenThrow(exception);
//        when(securityReadService.getMember().getPublicId()).thenReturn(null);

        MyCommentServiceRequest request = MyCommentServiceRequest.builder()
                .commentType(CommentType.BOARD)
                .orderType(null)
                .searchType(null)
                .search(null)
                .page(1)
                .size(10)
                .build();

        // when
        PlayHiveException actualException = assertThrows(PlayHiveException.class, () -> {
            commentReadService.getMyCommentList(request);
        });

        // then
        assertEquals(USER_NOT_FOUND, actualException.getErrorCode());
    }

    @Test
    @DisplayName("내 댓글 수 조회 - 정상 반환")
    void getMyCommentCount_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        // when
        long result = commentReadService.getMyCommentCount();

        // then
        assertThat(result).isEqualTo(7L);
    }

    @Test
    @DisplayName("댓글 ID 존재 확인 - 존재하는 경우")
    void existsById_true() {
        // given

        // when
        boolean result = commentReadService.existsById(commentList.get(0).getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("댓글 ID 존재 확인 - 존재하지 않는 경우")
    void existsById_false() {
        // given && when
        boolean result = commentReadService.existsById(NON_EXIST_ID);

        // then
        assertThat(result).isFalse();
    }
}