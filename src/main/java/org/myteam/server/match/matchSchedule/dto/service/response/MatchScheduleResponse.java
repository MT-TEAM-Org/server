package org.myteam.server.match.matchSchedule.dto.service.response;

import org.myteam.server.match.matchSchedule.domain.MatchSchedule;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchScheduleResponse {

	@Schema(description = "경기 ID")
	private Long id;
	private TeamResponse homeTeam;
	private TeamResponse awayTeam;
	@Schema(description = "경기 유형 ID")
	private String category;

	@Builder
	public MatchScheduleResponse(Long id, TeamResponse homeTeam, TeamResponse awayTeam, String category) {
		this.id = id;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.category = category;
	}

	public static MatchScheduleResponse createResponse(MatchSchedule matchSchedule) {
		return MatchScheduleResponse.builder()
			.id(matchSchedule.getId())
			.homeTeam(TeamResponse.createResponse(matchSchedule.getHomeTeam()))
			.awayTeam(TeamResponse.createResponse(matchSchedule.getAwayTeam()))
			.category(matchSchedule.getCategory().name())
			.build();
	}
}
