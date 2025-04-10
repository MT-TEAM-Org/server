package org.myteam.server.match.matchPrediction.dto.service.response;

import org.myteam.server.match.match.dto.service.response.TeamResponse;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.util.PercentageUtil;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchPredictionResponse {

	private Long id;
	private Long matchId;
	private TeamResponse homeTeam;
	private TeamResponse awayTeam;
	private int home;
	private int away;

	@Builder
	public MatchPredictionResponse(Long id, Long matchId, TeamResponse homeTeam, TeamResponse awayTeam, int home,
		int away) {
		this.id = id;
		this.matchId = matchId;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.home = home;
		this.away = away;
		updateAsPercentage();
	}

	public static MatchPredictionResponse createResponse(MatchPrediction matchPrediction) {
		return MatchPredictionResponse.builder()
			.id(matchPrediction.getId())
			.matchId(matchPrediction.getMatch().getId())
			.homeTeam(TeamResponse.createResponse(matchPrediction.getMatch().getHomeTeam()))
			.awayTeam(TeamResponse.createResponse(matchPrediction.getMatch().getAwayTeam()))
			.home(matchPrediction.getHome())
			.away(matchPrediction.getAway())
			.build();
	}

	private void updateAsPercentage() {
		int[] percentage = PercentageUtil.getCalculatedPercentages(this.home, this.away);
		this.home = percentage[0];
		this.away = percentage[1];
	}
}
