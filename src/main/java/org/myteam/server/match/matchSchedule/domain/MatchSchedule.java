package org.myteam.server.match.matchSchedule.domain;

import java.time.LocalDateTime;

import org.myteam.server.global.domain.Base;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_match_schedule")
public class MatchSchedule extends Base {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Team homeTeam;

	@ManyToOne(fetch = FetchType.LAZY)
	private Team awayTeam;

	@Enumerated(EnumType.STRING)
	private MatchCategory category;

	private LocalDateTime startTime;

	@Builder
	public MatchSchedule(Long id, Team homeTeam, Team awayTeam, MatchCategory category, LocalDateTime startTime) {
		this.id = id;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.category = category;
		this.startTime = startTime;
	}
}
