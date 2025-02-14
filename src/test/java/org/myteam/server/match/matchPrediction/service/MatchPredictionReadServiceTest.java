package org.myteam.server.match.matchPrediction.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.service.MatchReadService;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.springframework.beans.factory.annotation.Autowired;

class MatchPredictionReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchPredictionReadService matchPredictionReadService;

	@DisplayName("경기예측 현황을 조회한다.")
	@Test
	void findById() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatchPrediction(match, 1, 2);

		assertThat(matchPredictionReadService.findOne(match.getId()))
			.extracting("home", "away")
			.contains(33, 67);
	}

}
