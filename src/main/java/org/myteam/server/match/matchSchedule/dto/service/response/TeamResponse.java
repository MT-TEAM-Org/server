package org.myteam.server.match.matchSchedule.dto.service.response;

import org.myteam.server.match.team.domain.Team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamResponse {

	@Schema(description = "팀 ID")
	private int id;
	@Schema(description = "팀이름")
	private String name;
	@Schema(description = "팀 로고 url")
	private String logo;
	@Schema(description = "팀 유형")
	private String category;

	@Builder
	public TeamResponse(int id, String name, String logo, String category) {
		this.id = id;
		this.name = name;
		this.logo = logo;
		this.category = category;
	}

	public static TeamResponse createResponse(Team team) {
		return TeamResponse.builder()
			.id(team.getId())
			.name(team.getName())
			.logo(team.getLogo())
			.category(team.getCategory().name())
			.build();
	}
}
