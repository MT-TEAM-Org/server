package org.myteam.server.comment.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.dto.request.CommentRequest.CommentDeleteRequest;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.media.MediaUtils;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class CommentDeleteServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    private Member member;
    private Member otherUser;
    private UUID publicId;
    private List<Board> boardList = new ArrayList<>();
    private Comment comment;
    private final Long NON_EXIST_ID = 999999L;
    private Match match;

    @Transactional
    @BeforeEach
    void setUp() {
        member = createMember(0);
        otherUser = createMember(1);
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
                "img.com/image1.png",
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
    @DisplayName("조건1: 본인이 댓글 삭제하면 정상 처리")
    void deleteComment_success() {
        // given
        CommentDeleteRequest request = new CommentDeleteRequest(CommentType.BOARD);
        when(securityReadService.getMember()).thenReturn(member);
        when(redisCountService.getCommonCount(ServiceType.COMMENT, DomainType.BOARD, boardList.get(0).getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));

        try (MockedStatic<MediaUtils> utilities = mockStatic(MediaUtils.class)) {
            utilities.when(() -> MediaUtils.getImagePath(comment.getImageUrl()))
                    .thenReturn("image1.png");

            // when
            assertThatCode(() ->
                    commentService.deleteComment(boardList.get(0).getId(), comment.getId(), request)
            ).doesNotThrowAnyException();

            // then
            verify(s3Service).deleteFile("image1.png");
        }
    }

    @Test
    @DisplayName("조건2: 작성자가 아닌 경우 예외 발생")
    void deleteComment_notAuthor_throws() {
        Member otherUser = createMember(2);
        when(securityReadService.getMember()).thenReturn(otherUser);
        CommentDeleteRequest request = new CommentDeleteRequest(CommentType.BOARD);
        when(redisCountService.getCommonCount(ServiceType.COMMENT, DomainType.BOARD, boardList.get(0).getId(), null))
                .thenReturn(new CommonCountDto(0, 0, 0));

        try (MockedStatic<MediaUtils> utilities = mockStatic(MediaUtils.class)) {
            utilities.when(() -> MediaUtils.getImagePath(comment.getImageUrl()))
                    .thenReturn("image1.png");

            // when && then
            assertThatThrownBy(() ->
                    commentService.deleteComment(boardList.get(0).getId(), comment.getId(), request)
            ).isInstanceOf(PlayHiveException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }

    @Test
    @DisplayName("조건3: 대댓글에 이미지가 있을 경우 S3 삭제")
    void deleteComment_replyWithImage() {
        Comment reply = commentFactory.createComment(
                CommentType.BOARD,
                boardList.get(0).getId(),
                member,
                null,
                "reply",
                "img.com/reply.png",
                "127.0.0.1",
                comment.getId()
        );
        commentRepository.save(reply);

        CommentDeleteRequest request = new CommentDeleteRequest(CommentType.BOARD);
        when(securityReadService.getMember()).thenReturn(member);

        // when
        try (MockedStatic<MediaUtils> utilities = mockStatic(MediaUtils.class)) {
            utilities.when(() -> MediaUtils.getImagePath(comment.getImageUrl()))
                    .thenReturn("image1.png");
            utilities.when(() -> MediaUtils.getImagePath("img.com/reply.png"))
                    .thenReturn("reply.png");

            // when && then
            assertThatCode(() ->
                    commentService.deleteComment(1L, comment.getId(), request)
            ).doesNotThrowAnyException();

            // then
            verify(s3Service).deleteFile("image1.png");
            verify(s3Service).deleteFile("reply.png");
        }
    }

    @Test
    @DisplayName("조건4: MATCH 타입이면 카운트 감소 호출 안함")
    void deleteComment_matchType_noCount() {
        // given
        Comment matchComment = commentFactory.createComment(
                CommentType.MATCH,
                match.getId(),
                member,
                null,
                "comment",
                null,
                "127.0.0.1",
                null
        );
        commentRepository.save(matchComment);
        when(securityReadService.getMember()).thenReturn(member);
        CommentDeleteRequest request = new CommentDeleteRequest(CommentType.MATCH);

        // when
        commentService.deleteComment(match.getId(), matchComment.getId(), request);

        // then
        verify(redisCountService, never()).getCommonCount(any(), any(), any(), any());
    }
}