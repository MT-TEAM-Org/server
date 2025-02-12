package org.myteam.server.match.matchSchedule.dto.service.response;

import org.myteam.server.match.team.domain.Team;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamResponse {

	private int id;
	private String name;
	private String logo;
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
