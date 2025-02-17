package org.myteam.server.match.matchReply.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageableCustomResponse;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchComment.domain.MatchComment;
import org.myteam.server.match.matchReply.domain.MatchReply;
import org.myteam.server.match.matchReply.dto.repository.MatchReplyDto;
import org.myteam.server.match.matchReply.dto.service.request.MatchReplyServiceRequest;
import org.myteam.server.match.matchReply.dto.service.response.MatchReplyListResponse;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchReplyReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchReplyReadService matchReplyReadService;

	@DisplayName("경기 대댓글 ID로 경기 대댓글을 조회한다.")
	@Test
	void findByIdTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment = createMatchComment(match, member, "경기 댓글 테스트");

		MatchReply matchReply = createMatchReply(matchComment, member, "경기 대댓글 테스트");

		MatchReply findMatchReply = matchReplyReadService.findById(matchReply.getId());

		assertThat(findMatchReply)
			.extracting("id", "matchComment.id", "member.publicId", "comment")
			.contains(matchReply.getId(), matchComment.getId(), member.getPublicId(), "경기 대댓글 테스트");
	}

	@DisplayName("경기 댓글 ID로 조회시 존재하지 않으면 예외가 발생한다.")
	@Test
	void findByIdNotExistThrowExceptionTest() {
		assertThatThrownBy(() -> matchReplyReadService.findById(1L))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.MATCH_REPLY_NOT_FOUND.getMsg());
	}

	@DisplayName("댓글ID로 대댓글리스트를 조회한다.")
	@Test
	void findByMatchIdTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment = createMatchComment(match, member, "경기 댓글 테스트");

		MatchReply matchReply1 = createMatchReply(matchComment, member, "경기 대댓글 테스트1");
		MatchReply matchReply2 = createMatchReply(matchComment, member, "경기 대댓글 테스트2");
		MatchReply matchReply3 = createMatchReply(matchComment, member, "경기 대댓글 테스트3");

		MatchReplyServiceRequest matchReplyServiceRequest = MatchReplyServiceRequest.builder()
			.matchCommentId(matchComment.getId())
			.page(1)
			.size(2)
			.build();

		MatchReplyListResponse matchReplyListResponse = matchReplyReadService.findByMatchCommentId(
			matchReplyServiceRequest);

		List<MatchReplyDto> matchReplyList = matchReplyListResponse.getList().getContent();
		PageableCustomResponse pageInfo = matchReplyListResponse.getList().getPageInfo();

		assertAll(
			() -> assertThat(pageInfo)
				.extracting("currentPage", "totalPage", "totalElement")
				.containsExactlyInAnyOrder(
					1, 2, 3L
				),
			() -> assertThat(matchReplyList)
				.extracting("matchReplyId", "matchCommentId", "member.publicId", "member.nickName", "comment")
				.contains(
					Tuple.tuple(matchReply1.getId(), matchReply1.getMatchComment().getId(), matchReply1.getMember().getPublicId(),
						"test",
						"경기 대댓글 테스트1"),
					Tuple.tuple(matchReply2.getId(), matchReply2.getMatchComment().getId(), matchReply2.getMember().getPublicId(),
						"test",
						"경기 대댓글 테스트2")
				)
		);
	}
}
