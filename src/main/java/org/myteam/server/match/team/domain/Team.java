package org.myteam.server.match.team.domain;

import org.myteam.server.global.domain.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_team")
public class Team extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private String logo;

	@Enumerated(EnumType.STRING)
	private TeamCategory category;

	@Builder
	public Team(int id, String name, String logo, TeamCategory category) {
		this.id = id;
		this.name = name;
		this.logo = logo;
		this.category = category;
	}
}
