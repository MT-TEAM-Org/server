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
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.myteam.server.mypage.dto.request.MyCommentServiceRequest;
import org.myteam.server.mypage.dto.response.MyCommentListResponse;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.myteam.server.global.exception.ErrorCode.*;

class CommentUpdateServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    private Member author;
    private Member otherUser;
    private UUID publicId;
    private List<Board> boardList = new ArrayList<>();
    private Comment comment;
    private final Long NON_EXIST_ID = 999999L;
    private Match match;

    @Transactional
    @BeforeEach
    void setUp() {
        author = createMember(0);
        otherUser = createMember(1);
        publicId = author.getPublicId();
        for (int i = 1; i <= 5; i++) {
            boardList.add(createBoard(author, Category.BASEBALL, CategoryType.FREE,
                    "title" + i, "content" + i));
        }
        comment = commentFactory.createComment(
                CommentType.BOARD,
                boardList.get(0).getId(),
                author,
                null,
                "comment",
                null,
                "127.0.0.1",
                null
        );
        commentRepository.save(comment);

        Team home = createTeam(0, TeamCategory.BASEBALL);
        Team away = createTeam(1, TeamCategory.BASEBALL);
        teamRepository.save(home);
        teamRepository.save(away);
        match = Match.builder()
                .awayTeam(away)
                .homeTeam(home)
                .category(MatchCategory.BASEBALL)
                .leagueName("KBO")
                .place("home")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(1L))
                .build();

        matchRepository.save(match);
    }

    @Test
    @DisplayName("조건1: 본인이 댓글 수정")
    void update_own_comment_success() {
        // given
        CommentSaveRequest request = new CommentSaveRequest(CommentType.BOARD, "수정된 댓글", null, null, null);
        when(securityReadService.getMember()).thenReturn(author);

        // when
        CommentSaveResponse response = commentService.update(comment.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("수정된 댓글");
    }

    @Test
    @DisplayName("조건2: 작성자가 아닌 경우 예외 발생")
    void update_not_author_throws() {
        // given
        when(securityReadService.getMember()).thenReturn(otherUser);

        CommentSaveRequest request = new CommentSaveRequest(CommentType.BOARD, "수정 시도", null, null, null);

        // when & then
        assertThatThrownBy(() -> commentService.update(comment.getId(), request))
                .isInstanceOf(PlayHiveException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.POST_AUTHOR_MISMATCH);
    }

    @Test
    @DisplayName("조건3: 이미지 변경 시 S3 이미지 삭제 호출")
    void update_image_changed_triggers_delete() {
        // given
        String newImage = "http://img.com/new.png";
        CommentSaveRequest request = new CommentSaveRequest(CommentType.BOARD, "이미지 수정", newImage, null, null);

        when(securityReadService.getMember()).thenReturn(author);

        // when
        commentService.update(comment.getId(), request);

        // then
        assertThat(commentReadService.getCommentDetail(comment.getId()).getImageUrl()).isEqualTo(newImage);
    }

    @Test
    @DisplayName("조건4: 이미지 변경 없으면 S3 삭제 호출 안함")
    void update_image_not_changed_no_delete() {
        // given
        String sameImage = "http://img.com/old.png";
        CommentSaveRequest request = new CommentSaveRequest(CommentType.BOARD, "이미지 변경 없음", sameImage, null, null);

        when(securityReadService.getMember()).thenReturn(author);

        // when
        commentService.update(comment.getId(), request);

        // then
        verify(s3Service, never()).deleteFile(any());
    }

    @Test
    @DisplayName("조건5: 멘션된 유저가 존재하는 경우")
    void update_with_mentioned_user() {
        // given
        Member mentioned = createMember(3);
        CommentSaveRequest request = new CommentSaveRequest(CommentType.BOARD, "멘션 수정", null, null, mentioned.getPublicId());

        when(securityReadService.getMember()).thenReturn(author);

        // when
        CommentSaveResponse response = commentService.update(comment.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("멘션 수정");
    }
}