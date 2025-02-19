package org.myteam.server.match.matchComment.service;

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
import org.myteam.server.match.matchComment.dto.repository.MatchCommentDto;
import org.myteam.server.match.matchComment.dto.service.request.MatchCommentServiceRequest;
import org.myteam.server.match.matchComment.dto.service.response.MatchCommentListResponse;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.dto.repository.NewsCommentDto;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentServiceRequest;
import org.myteam.server.news.newsComment.dto.service.response.NewsCommentListResponse;
import org.myteam.server.news.newsComment.service.NewsCommentReadService;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchCommentReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchCommentReadService matchCommentReadService;

	@DisplayName("경기 댓글 ID로 경기댓글을 조회한다.")
	@Test
	void findByIdTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment = createMatchComment(match, member, "경기 댓글 테스트");

		MatchComment findMatchComment = matchCommentReadService.findById(matchComment.getId());

		assertThat(findMatchComment)
			.extracting("id", "match.id", "member.publicId", "comment")
			.contains(matchComment.getId(), match.getId(), member.getPublicId(), "경기 댓글 테스트");
	}

	@DisplayName("경기 댓글 ID로 조회시 조회하지 않으면 예외가 발생한다.")
	@Test
	void findByIdNotExistThrowExceptionTest() {
		assertThatThrownBy(() -> matchCommentReadService.findById(1L))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.MATCH_COMMENT_NOT_FOUND.getMsg());
	}

	@DisplayName("경기ID로 댓글리스트를 조회한다.")
	@Test
	void findByMatchIdTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment1 = createMatchComment(match, member, "경기 댓글 테스트1");
		MatchComment matchComment2 = createMatchComment(match, member, "경기 댓글 테스트2");

		MatchCommentServiceRequest matchCommentServiceRequest = MatchCommentServiceRequest.builder()
			.matchId(match.getId())
			.page(1)
			.size(10)
			.build();

		MatchCommentListResponse matchCommentListResponse = matchCommentReadService.findByMatchId(
			matchCommentServiceRequest);

		List<MatchCommentDto> matchCommentList = matchCommentListResponse.getList().getContent();
		PageableCustomResponse pageInfo = matchCommentListResponse.getList().getPageInfo();

		assertAll(
			() -> assertThat(pageInfo)
				.extracting("currentPage", "totalPage", "totalElement")
				.containsExactlyInAnyOrder(
					1, 1, 2L
				),
			() -> assertThat(matchCommentList)
				.extracting("matchCommentId", "matchId", "memberDto.publicId", "memberDto.nickName", "comment")
				.contains(
					Tuple.tuple(matchComment1.getId(), matchComment1.getMatch().getId(), matchComment1.getMember().getPublicId(),
						"test",
						"경기 댓글 테스트1"),
					Tuple.tuple(matchComment2.getId(), matchComment2.getMatch().getId(), matchComment2.getMember().getPublicId(),
						"test",
						"경기 댓글 테스트2")
				)
		);
	}
}
