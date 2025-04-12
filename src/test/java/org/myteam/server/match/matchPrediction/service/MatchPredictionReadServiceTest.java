package org.myteam.server.match.matchPrediction.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchPrediction.dto.service.response.MatchPredictionResponse;
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

		MatchPredictionResponse matchPredictionResponse = matchPredictionReadService.findOne(match.getId());

		assertThat(matchPredictionResponse)
			.extracting("home", "away", "homeTeam.name", "homeTeam.logo", "homeTeam.category",
				"awayTeam.name", "awayTeam.logo", "awayTeam.category")
			.contains(33, 67, team1.getName(), team1.getLogo(), team1.getCategory().name(),
				team2.getName(), team2.getLogo(), team2.getCategory().name());
	}

}
