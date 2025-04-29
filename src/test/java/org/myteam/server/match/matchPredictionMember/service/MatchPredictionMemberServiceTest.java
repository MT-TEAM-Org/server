package org.myteam.server.match.matchPredictionMember.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.match.matchPredictionMember.dto.service.response.MatchPredictionMemberDeleteResponse;
import org.myteam.server.match.matchPredictionMember.dto.service.response.MatchPredictionMemberResponse;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MatchPredictionMemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchPredictionMemberService matchPredictionMemberService;

	private Team home;
	private Team away;
	private Match match;
	private MatchPrediction matchPrediction;
	private Member member;

	@Transactional
	@BeforeEach
	void setUp() {
		home = createTeam(1, TeamCategory.FOOTBALL);
		away = createTeam(2, TeamCategory.FOOTBALL);
		match = createMatch(home, away, MatchCategory.FOOTBALL, LocalDateTime.now());
		matchPrediction = createMatchPrediction(match, 1, 2);
		member = createMember(1);
	}

	@DisplayName("사용자 추천 데이터를 추가한다.")
	@Test
	void saveTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		MatchPrediction matchPrediction = createMatchPrediction(match, 1, 2);

		Member member = createMember(1);

		matchPredictionMemberService.save(matchPrediction.getId(), Side.HOME);

		assertThat(matchPredictionMemberRepository.findByMatchPredictionIdAndMemberPublicId(matchPrediction.getId(), member.getPublicId()).get())
				.extracting("matchPrediction.id", "member.publicId")
				.contains(matchPrediction.getId(), member.getPublicId());
	}

	@DisplayName("사용자 추천 데이터를 삭제한다.")
	@Test
	void deleteByNewsIdMemberIdTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		MatchPrediction matchPrediction = createMatchPrediction(match, 1, 2);

		Member member = createMember(1);

		MatchPredictionMember matchPredictionMember = createMatchPredictionMember(member, matchPrediction);

		matchPredictionMemberService.deleteByMatchPredictionIdMemberId(matchPrediction.getId());

		assertThatThrownBy(() -> matchPredictionMemberRepository.findById(matchPredictionMember.getId()).get())
				.isInstanceOf(NoSuchElementException.class)
				.hasMessage("No value present");
	}

	@Test
	@DisplayName("1. 경기 예측 참여 성공")
	void save_success() {
		// given
		when(securityReadService.getMember()).thenReturn(member);

		// when
		MatchPredictionMemberResponse response = matchPredictionMemberService.save(matchPrediction.getId(), Side.HOME);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getMatchPredictionId()).isEqualTo(matchPrediction.getId());
	}

	@Test
	@DisplayName("2. 경기 예측 취소 성공")
	void delete_success() {
		// given
		createMatchPredictionMember(member, matchPrediction);
		when(securityReadService.getMember()).thenReturn(member);

		// when
		MatchPredictionMemberDeleteResponse response = matchPredictionMemberService.deleteByMatchPredictionIdMemberId(matchPrediction.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.getMatchPredictionId()).isEqualTo(matchPrediction.getId());
	}
}
