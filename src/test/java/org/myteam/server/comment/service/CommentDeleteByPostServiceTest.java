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

class CommentDeleteByPostServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    private Member author;
    private Member otherUser;
    private UUID publicId;
    private List<Board> boardList = new ArrayList<>();
    private Comment comment;
    private Comment commentByImg;
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

        commentByImg = commentFactory.createComment(
                CommentType.BOARD,
                boardList.get(1).getId(),
                author,
                null,
                "comment",
                "img",
                "127.0.0.1",
                null
        );
        commentRepository.save(commentByImg);

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
    @DisplayName("조건1: 댓글이 없는 경우 → 아무 작업도 수행되지 않음")
    void deleteCommentByPost_noComments() {
        commentService.deleteCommentByPost(CommentType.BOARD, boardList.get(3).getId());

        verify(s3Service, never()).deleteFile(anyString());
    }

    @Test
    @DisplayName("조건2: 댓글은 있지만 이미지가 없는 경우")
    void deleteCommentByPost_commentsWithoutImage() {
        commentService.deleteCommentByPost(CommentType.BOARD, boardList.get(0).getId());

        verify(s3Service, never()).deleteFile(anyString());
    }

    @Test
    @DisplayName("조건3: 댓글에 이미지가 있는 경우 → S3 이미지 삭제")
    void deleteCommentByPost_commentsWithImage() {

        commentService.deleteCommentByPost(CommentType.BOARD, boardList.get(1).getId());

        verify(s3Service).deleteFile(commentByImg.getImageUrl());
    }
}