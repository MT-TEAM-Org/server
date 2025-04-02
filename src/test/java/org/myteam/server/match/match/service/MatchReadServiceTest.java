package org.myteam.server.match.match.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.dto.service.response.MatchEsportsScheduleResponse;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class MatchReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchReadService matchReadService;
	@MockBean
	private MatchYoutubeService matchYoutubeService;

	@DisplayName("전체 경기일정 목록을 조회한다.")
	@Test
	void findSchedulesBetweenDateTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);
		Team team3 = createTeam(3, TeamCategory.ESPORTS);
		Team team4 = createTeam(4, TeamCategory.ESPORTS);

		createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDateTime.now());
		createMatch(team2, team3, MatchCategory.FOOTBALL, LocalDateTime.now());
		createMatch(team3, team4, MatchCategory.ESPORTS, LocalDateTime.now());

		assertThat(matchReadService.findSchedulesBetweenDate(MatchCategory.ALL).getList())
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
				)
			);
	}

	@DisplayName("이전날짜나 일주일 이후의 경기는 조회되지 않는다.")
	@Test
	void findSchedulesBetweenDateTest2() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);
		Team team3 = createTeam(3, TeamCategory.FOOTBALL);

		createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().minusDays(1).atStartOfDay());
		createMatch(team2, team3, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatch(team3, team1, MatchCategory.FOOTBALL,
			LocalDate.now().plusWeeks(1).atTime(LocalTime.of(7, 0)));

		assertThat(matchReadService.findSchedulesBetweenDate(MatchCategory.FOOTBALL).getList())
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

		createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatch(team2, team1, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
		createMatch(team3, team4, MatchCategory.ESPORTS, LocalDate.now().atStartOfDay());

		assertThat(matchReadService.findSchedulesBetweenDate(MatchCategory.FOOTBALL).getList())
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

	@DisplayName("경기를 상세 조회 한다.")
	@Test
	void findById() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		assertThat(matchReadService.findOne(match.getId()))
			.extracting(
				"homeTeam.name", "homeTeam.logo", "homeTeam.category",
				"awayTeam.name", "awayTeam.logo", "awayTeam.category",
				"category")
			.contains(
				team1.getName(), team1.getLogo(), team1.getCategory().name(),
				team2.getName(), team2.getLogo(), team2.getCategory().name(),
				MatchCategory.FOOTBALL.name()
			);
	}

	@DisplayName("경기를 상세 조회시 데이터가 없으면 예외가 발생한다.")
	@Test
	void findByIdThrowException() {
		assertThatThrownBy(() -> matchReadService.findOne(1L))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.MATCH_NOT_FOUNT.getMsg());
	}

	@DisplayName("E스포츠 경기시간이 아직 되지 않았으면 Youtube API를 조회하지 않는다.")
	@Test
	void confirmEsportsYoutubeNotAfterStartDate() {
		Team team1 = createTeam(1, TeamCategory.ESPORTS);
		Team team2 = createTeam(2, TeamCategory.ESPORTS);

		createMatch(team1, team2, MatchCategory.ESPORTS, LocalDateTime.now().plusHours(2));

		assertThat(matchReadService.confirmEsportsYoutube())
			.extracting("isLive", "url")
			.containsExactly(false, null);
	}

	@DisplayName("E스포츠 경기시간이 되었으면 Youtube API를 조회한다.")
	@Test
	void confirmEsportsYoutubeAfterStartDate() {
		given(matchYoutubeService.getUrl())
			.willReturn("www.test.com");

		Team team1 = createTeam(1, TeamCategory.ESPORTS);
		Team team2 = createTeam(2, TeamCategory.ESPORTS);

		createMatch(team1, team2, MatchCategory.ESPORTS, LocalDateTime.now().plusHours(1));

		assertThat(matchReadService.confirmEsportsYoutube())
			.extracting("isLive", "url")
			.containsExactly(true, "www.test.com");
	}

	@DisplayName("ESPORTS 경기일정 목록을 조회한다.")
	@Test
	void findEsportsSchedulesBetweenDateTest() {
		Team team1 = createTeam(1, TeamCategory.ESPORTS);
		Team team2 = createTeam(2, TeamCategory.ESPORTS);
		Team team3 = createTeam(3, TeamCategory.ESPORTS);
		Team team4 = createTeam(4, TeamCategory.ESPORTS);

		createMatch(team1, team2, MatchCategory.ESPORTS, LocalDateTime.now());
		createMatch(team2, team3, MatchCategory.ESPORTS, LocalDateTime.now());
		createMatch(team3, team4, MatchCategory.ESPORTS, LocalDateTime.now());

		createMatch(team1, team2, MatchCategory.ESPORTS, LocalDateTime.now().plusDays(1));
		createMatch(team2, team3, MatchCategory.ESPORTS, LocalDateTime.now().plusDays(1));
		createMatch(team3, team4, MatchCategory.ESPORTS, LocalDateTime.now().plusDays(1));

		List<MatchEsportsScheduleResponse> response = matchReadService.findEsportsSchedulesBetweenDate();
		assertThat(response)
			.flatExtracting(MatchEsportsScheduleResponse::getList) // matches 리스트에서 각 MatchResponse를 가져옴
			.extracting(
				matchResponse -> matchResponse.getHomeTeam().getName(),
				matchResponse -> matchResponse.getHomeTeam().getLogo(),
				matchResponse -> matchResponse.getHomeTeam().getCategory(),
				matchResponse -> matchResponse.getAwayTeam().getName(),
				matchResponse -> matchResponse.getAwayTeam().getLogo(),
				matchResponse -> matchResponse.getAwayTeam().getCategory()
			)
			.containsExactly(
				tuple(team1.getName(), team1.getLogo(), team1.getCategory().name(),
					team2.getName(), team2.getLogo(), team2.getCategory().name()),
				tuple(team2.getName(), team2.getLogo(), team2.getCategory().name(),
					team3.getName(), team3.getLogo(), team3.getCategory().name()),
				tuple(team3.getName(), team3.getLogo(), team3.getCategory().name(),
					team4.getName(), team4.getLogo(), team4.getCategory().name()),
				tuple(team1.getName(), team1.getLogo(), team1.getCategory().name(),
					team2.getName(), team2.getLogo(), team2.getCategory().name()),
				tuple(team2.getName(), team2.getLogo(), team2.getCategory().name(),
					team3.getName(), team3.getLogo(), team3.getCategory().name()),
				tuple(team3.getName(), team3.getLogo(), team3.getCategory().name(),
					team4.getName(), team4.getLogo(), team4.getCategory().name())
			);
	}

}
