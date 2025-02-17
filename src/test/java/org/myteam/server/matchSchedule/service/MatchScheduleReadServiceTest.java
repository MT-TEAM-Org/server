package org.myteam.server.matchSchedule.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.match.matchSchedule.domain.MatchCategory;
import org.myteam.server.match.matchSchedule.service.MatchScheduleReadService;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.springframework.beans.factory.annotation.Autowired;

class MatchScheduleReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchScheduleReadService matchScheduleReadService;

	@DisplayName("전체 경기일정 목록을 조회한다.")
	@Test
	void findSchedulesBetweenDateTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);
		Team team3 = createTeam(3, TeamCategory.ESPORTS);
		Team team4 = createTeam(4, TeamCategory.ESPORTS);

		createMatchSchedule(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatchSchedule(team2, team3, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatchSchedule(team3, team4, MatchCategory.ESPORTS, LocalDate.now().atStartOfDay());

		assertThat(matchScheduleReadService.findSchedulesBetweenDate(null).getList())
			.extracting(
				"homeTeam.name", "homeTeam.logo", "homeTeam.category",
				"awayTeam.name", "awayTeam.logo", "awayTeam.category",
				"category")
			.containsExactly(
				tuple(
					team1.getName(), team1.getLogo(), team1.getCategory().name(),
					team2.getName(), team2.getLogo(), team2.getCategory().name(),
					MatchCategory.FOOTBALL.name()
				),
				tuple(
					team2.getName(), team2.getLogo(), team2.getCategory().name(),
					team3.getName(), team3.getLogo(), team3.getCategory().name(),
					MatchCategory.FOOTBALL.name()
				),
				tuple(
					team3.getName(), team3.getLogo(), team3.getCategory().name(),
					team4.getName(), team4.getLogo(), team4.getCategory().name(),
					MatchCategory.ESPORTS.name()
				)
			);
	}

	@DisplayName("이전날짜나 다음날 새벽 6시 이후의 경기는 조회되지 않는다.")
	@Test
	void findSchedulesBetweenDateTest2() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);
		Team team3 = createTeam(3, TeamCategory.FOOTBALL);

		createMatchSchedule(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().minusDays(1).atStartOfDay());
		createMatchSchedule(team2, team3, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatchSchedule(team3, team1, MatchCategory.FOOTBALL,
			LocalDate.now().plusDays(1).atTime(LocalTime.of(7, 0)));

		assertThat(matchScheduleReadService.findSchedulesBetweenDate(MatchCategory.FOOTBALL).getList())
			.extracting(
				"homeTeam.name", "homeTeam.logo", "homeTeam.category",
				"awayTeam.name", "awayTeam.logo", "awayTeam.category",
				"category")
			.containsExactly(
				tuple(
					team2.getName(), team2.getLogo(), team2.getCategory().name(),
					team3.getName(), team3.getLogo(), team3.getCategory().name(),
					MatchCategory.FOOTBALL.name()
				)
			);
	}

	@DisplayName("축구 경기일정만 조회한다.")
	@Test
	void findSchedulesBetweenDateFootballTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);
		Team team3 = createTeam(3, TeamCategory.ESPORTS);
		Team team4 = createTeam(4, TeamCategory.ESPORTS);

		createMatchSchedule(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatchSchedule(team2, team1, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatchSchedule(team3, team4, MatchCategory.ESPORTS, LocalDate.now().atStartOfDay());

		assertThat(matchScheduleReadService.findSchedulesBetweenDate(MatchCategory.FOOTBALL).getList())
			.extracting(
				"homeTeam.name", "homeTeam.logo", "homeTeam.category",
				"awayTeam.name", "awayTeam.logo", "awayTeam.category",
				"category")
			.containsExactly(
				tuple(
					team1.getName(), team1.getLogo(), team1.getCategory().name(),
					team2.getName(), team2.getLogo(), team2.getCategory().name(),
					MatchCategory.FOOTBALL.name()
				),
				tuple(
					team2.getName(), team2.getLogo(), team2.getCategory().name(),
					team1.getName(), team1.getLogo(), team1.getCategory().name(),
					MatchCategory.FOOTBALL.name()
				)
			);
	}

}
