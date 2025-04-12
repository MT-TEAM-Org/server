package org.myteam.server.match.matchPredictionMember.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberReadService;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchPredictionMemberReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchPredictionMemberReadService matchPredictionMemberReadService;

	@DisplayName("이미 좋아요를 누른 뉴스면 예외가 발생한다.")
	@Test
	void confirmExistMemberTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		MatchPrediction matchPrediction = createMatchPrediction(match, 1, 2);

		Member member = createMember(1);

		createMatchPredictionMember(member, matchPrediction);

		assertThatThrownBy(() -> matchPredictionMemberReadService.confirmExistMember(matchPrediction.getId(), member.getPublicId()))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.ALREADY_MEMBER_MATCH_PREDICTION.getMsg());

	}

}
