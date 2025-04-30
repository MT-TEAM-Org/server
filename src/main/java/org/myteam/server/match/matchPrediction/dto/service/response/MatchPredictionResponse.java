package org.myteam.server.match.matchPrediction.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.match.match.dto.service.response.TeamResponse;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
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
	private LocalDateTime startTime;
	private int home;
	private int away;
	private boolean isVote;
	private Side side;

	@Builder
	public MatchPredictionResponse(Long id, Long matchId, TeamResponse homeTeam, TeamResponse awayTeam, LocalDateTime startTime, int home,
		int away, boolean isVote, Side side) {
		this.id = id;
		this.matchId = matchId;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.startTime = startTime;
		this.home = home;
		this.away = away;
		this.isVote = isVote;
		this.side = side;
		updateAsPercentage();
	}

	public static MatchPredictionResponse createResponse(MatchPrediction matchPrediction, boolean isVote, Side side) {
		return MatchPredictionResponse.builder()
			.id(matchPrediction.getId())
			.matchId(matchPrediction.getMatch().getId())
			.homeTeam(TeamResponse.createResponse(matchPrediction.getMatch().getHomeTeam()))
			.awayTeam(TeamResponse.createResponse(matchPrediction.getMatch().getAwayTeam()))
			.startTime(matchPrediction.getMatch().getStartTime())
			.home(matchPrediction.getHome())
			.away(matchPrediction.getAway())
			.isVote(isVote)
			.side(side)
			.build();
	}

	private void updateAsPercentage() {
		int[] percentage = PercentageUtil.getCalculatedPercentages(this.home, this.away);
		this.home = percentage[0];
		this.away = percentage[1];
	}
}
