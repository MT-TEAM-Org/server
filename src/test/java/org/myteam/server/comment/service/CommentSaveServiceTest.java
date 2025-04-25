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

class CommentSaveServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    private Member member;
    private UUID publicId;
    private List<Board> boardList = new ArrayList<>();
    private Comment comment;
    private final Long NON_EXIST_ID = 999999L;
    private Match match;

    @Transactional
    @BeforeEach
    void setUp() {
        member = createMember(0);
        publicId = member.getPublicId();
        for (int i = 1; i <= 5; i++) {
            boardList.add(createBoard(member, Category.BASEBALL, CategoryType.FREE,
                    "title" + i, "content" + i));
        }
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
    @DisplayName("조건1: 멘션 없음 + 일반 타입 댓글 작성")
    void addComment_noMention_normalType() {
        // given
        Long contentId = boardList.get(0).getId();
        CommentSaveRequest request = new CommentSaveRequest(
                CommentType.BOARD, "댓글입니다.", null, null, null
        );
        when(securityReadService.getMember()).thenReturn(member);
        when(redisCountService.getCommonCount(ServiceType.COMMENT, DomainType.BOARD, contentId, null))
                .thenReturn(new CommonCountDto(0, 1, 0));

        // when
        CommentSaveResponse response = commentService.addComment(contentId, request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("댓글입니다.");
    }

    @Test
    @DisplayName("조건2: 멘션된 유저가 존재하는 댓글 작성")
    void addComment_withMention() {
        // given
        Member mentioned = createMember(1);
        Long contentId = boardList.get(0).getId();
        CommentSaveRequest request = new CommentSaveRequest(CommentType.BOARD, "멘션 댓글", null, null, mentioned.getPublicId());
        when(securityReadService.getMember()).thenReturn(member);
        when(redisCountService.getCommonCount(ServiceType.COMMENT, DomainType.BOARD, contentId, null))
                .thenReturn(new CommonCountDto(0, 1, 0));

        // when
        CommentSaveResponse response = commentService.addComment(contentId, request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("멘션 댓글");
    }

    @Test
    @DisplayName("조건3: MATCH 타입은 댓글 수 증가 호출 안함")
    void addComment_matchType() {
        // given
        Long contentId = match.getId();
        CommentSaveRequest request = new CommentSaveRequest(CommentType.MATCH, "매치 댓글", null, null, null);
        when(securityReadService.getMember()).thenReturn(member);

        // when
        CommentSaveResponse response = commentService.addComment(contentId, request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }
}