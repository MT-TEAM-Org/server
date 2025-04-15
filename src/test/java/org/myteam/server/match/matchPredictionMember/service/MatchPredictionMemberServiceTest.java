package org.myteam.server.match.matchPredictionMember.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberService;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchPredictionMemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchPredictionMemberService matchPredictionMemberService;

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
}
