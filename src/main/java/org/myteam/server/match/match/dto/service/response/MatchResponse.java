package org.myteam.server.match.match.dto.service.response;

import org.myteam.server.match.match.domain.Match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchResponse {

	@Schema(description = "경기 ID")
	private Long id;
	private TeamResponse homeTeam;
	private TeamResponse awayTeam;
	@Schema(description = "경기 유형 ID")
	private String category;

	@Builder
	public MatchResponse(Long id, TeamResponse homeTeam, TeamResponse awayTeam, String category) {
		this.id = id;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.category = category;
	}

	public static MatchResponse createResponse(Match match) {
		return MatchResponse.builder()
			.id(match.getId())
			.homeTeam(TeamResponse.createResponse(match.getHomeTeam()))
			.awayTeam(TeamResponse.createResponse(match.getAwayTeam()))
			.category(match.getCategory().name())
			.build();
	}
}
