package org.myteam.server.match.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.TestContainerSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.dto.service.response.MatchEsportsScheduleResponse;
import org.myteam.server.match.match.dto.service.response.MatchEsportsYoutubeResponse;
import org.myteam.server.match.match.dto.service.response.MatchResponse;
import org.myteam.server.match.match.dto.service.response.MatchScheduleListResponse;
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
        // given
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);
        Team team3 = createTeam(3, TeamCategory.ESPORTS);
        Team team4 = createTeam(4, TeamCategory.ESPORTS);

        createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDateTime.now());
        createMatch(team2, team3, MatchCategory.FOOTBALL, LocalDateTime.now());
        createMatch(team3, team4, MatchCategory.ESPORTS, LocalDateTime.now());

        // when
        MatchScheduleListResponse schedules = matchReadService.findSchedulesBetweenDate(MatchCategory.ALL);

        // then
        assertThat(schedules.getList())
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
        // given
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);
        Team team3 = createTeam(3, TeamCategory.FOOTBALL);

        createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDateTime.now().minusDays(1));
        createMatch(team2, team3, MatchCategory.FOOTBALL, LocalDateTime.now());
        createMatch(team3, team1, MatchCategory.FOOTBALL,
                LocalDate.now().plusWeeks(1).atTime(LocalTime.of(7, 0)));

        // when
        MatchScheduleListResponse schedules = matchReadService.findSchedulesBetweenDate(MatchCategory.FOOTBALL);

        // then
        assertThat(schedules.getList())
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


    @DisplayName("FOOTBALL 카테고리 경기 일정만 조회")
    @Test
    void findSchedulesBetweenDate_football_success() {
        // given
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);
        Team team3 = createTeam(3, TeamCategory.ESPORTS);

        createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDateTime.now());
        createMatch(team2, team3, MatchCategory.ESPORTS, LocalDateTime.now());

        // when
        MatchScheduleListResponse response = matchReadService.findSchedulesBetweenDate(MatchCategory.FOOTBALL);

        // then
        assertThat(response.getList())
                .extracting(
                        "homeTeam.name", "awayTeam.name", "category"
                )
                .containsExactly(
                        tuple(team1.getName(), team2.getName(), MatchCategory.FOOTBALL.name())
                );
    }

    @DisplayName("축구 경기일정만 조회한다.")
    @Test
    void findSchedulesBetweenDateFootballTest() {
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);
        Team team3 = createTeam(3, TeamCategory.ESPORTS);
        Team team4 = createTeam(4, TeamCategory.ESPORTS);

        createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDateTime.now());
        createMatch(team2, team1, MatchCategory.FOOTBALL, LocalDateTime.now());
        createMatch(team3, team4, MatchCategory.ESPORTS, LocalDateTime.now());

        MatchScheduleListResponse schedules = matchReadService.findSchedulesBetweenDate(MatchCategory.FOOTBALL);

        assertThat(schedules.getList())
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
                .extracting("isLive", "videoId")
                .containsExactly(false, null);
    }

    @DisplayName("E스포츠 경기시간이 되었으면 Youtube API를 조회한다.")
    @Test
    void confirmEsportsYoutubeAfterStartDate() {
        given(matchYoutubeService.getVideoId())
                .willReturn("wsawcwdwd");

        Team team1 = createTeam(1, TeamCategory.ESPORTS);
        Team team2 = createTeam(2, TeamCategory.ESPORTS);

        createMatch(team1, team2, MatchCategory.ESPORTS, LocalDateTime.now().plusHours(1));

        assertThat(matchReadService.confirmEsportsYoutube())
                .extracting("isLive", "videoId")
                .containsExactly(true, "wsawcwdwd");
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

        List<MatchEsportsScheduleResponse> response = matchReadService.findSchedulesBetweenDate(MatchCategory.ESPORTS);
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

    @DisplayName("존재하지 않는 경기를 조회하면 예외 발생")
    @Test
    void findOne_throwException() {
        // when & then
        assertThatThrownBy(() -> matchReadService.findOne(999L))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.MATCH_NOT_FOUNT.getMsg());
    }

    @DisplayName("E-스포츠 경기 시작 전이면 Youtube 조회 X")
    @Test
    void confirmEsportsYoutube_beforeStart() {
        // given
        Team home = createTeam(1, TeamCategory.ESPORTS);
        Team away = createTeam(2, TeamCategory.ESPORTS);
        createMatch(home, away, MatchCategory.ESPORTS, LocalDateTime.now().plusHours(2)); // 아직 시작 안 함

        // when
        MatchEsportsYoutubeResponse response = matchReadService.confirmEsportsYoutube();

        // then
        assertThat(response.isLive()).isFalse();
        assertThat(response.getVideoId()).isNull();
    }

    @DisplayName("E-스포츠 경기 시작되면 Youtube 조회 O")
    @Test
    void confirmEsportsYoutube_afterStart() {
        // given
        Team home = createTeam(1, TeamCategory.ESPORTS);
        Team away = createTeam(2, TeamCategory.ESPORTS);
        createMatch(home, away, MatchCategory.ESPORTS, LocalDateTime.now().minusHours(1)); // 이미 시작함
        given(matchYoutubeService.getVideoId()).willReturn("testVideoId");

        // when
        MatchEsportsYoutubeResponse response = matchReadService.confirmEsportsYoutube();

        // then
        assertThat(response.isLive()).isTrue();
        assertThat(response.getVideoId()).isEqualTo("testVideoId");
    }

    @DisplayName("E-스포츠 경기 스케줄 목록 조회")
    @Test
    void findSchedulesBetweenDate_esports_success() {
        // given
        Team team1 = createTeam(1, TeamCategory.ESPORTS);
        Team team2 = createTeam(2, TeamCategory.ESPORTS);

        createMatch(team1, team2, MatchCategory.ESPORTS, LocalDateTime.now());
        createMatch(team2, team1, MatchCategory.ESPORTS, LocalDateTime.now().plusDays(1));

        // when
        List<MatchEsportsScheduleResponse> response = matchReadService.findSchedulesBetweenDate(MatchCategory.ESPORTS);

        // then
        assertThat(response)
                .flatExtracting(MatchEsportsScheduleResponse::getList)
                .extracting(
                        match -> match.getHomeTeam().getName(),
                        match -> match.getAwayTeam().getName()
                )
                .containsExactlyInAnyOrder(
                        tuple(team1.getName(), team2.getName()),
                        tuple(team2.getName(), team1.getName())
                );
    }

    @DisplayName("ALL 카테고리 전체 경기일정 목록 조회")
    @Test
    void findSchedulesBetweenDate_all_success() {
        // given
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);
        Team team3 = createTeam(3, TeamCategory.ESPORTS);
        Team team4 = createTeam(4, TeamCategory.ESPORTS);

        createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDateTime.now());
        createMatch(team2, team3, MatchCategory.FOOTBALL, LocalDateTime.now());
        createMatch(team3, team4, MatchCategory.ESPORTS, LocalDateTime.now());

        // when
        MatchScheduleListResponse response = matchReadService.findSchedulesBetweenDate(MatchCategory.ALL);

        // then
        assertThat(response.getList())
                .extracting(
                        "homeTeam.name", "awayTeam.name", "category"
                )
                .containsExactlyInAnyOrder(
                        tuple(team1.getName(), team2.getName(), MatchCategory.FOOTBALL.name()),
                        tuple(team2.getName(), team3.getName(), MatchCategory.FOOTBALL.name())
                );
    }

    @DisplayName("기간이 벗어난 경기는 조회되지 않는다.")
    @Test
    void findSchedulesBetweenDate_exclude_invalid_dates() {
        // given
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);

        createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDateTime.now().minusDays(1)); // 제외
        createMatch(team2, team1, MatchCategory.FOOTBALL, LocalDateTime.now()); // 포함
        createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDateTime.now().plusWeeks(2)); // 제외

        // when
        MatchScheduleListResponse response = matchReadService.findSchedulesBetweenDate(MatchCategory.FOOTBALL);

        // then
        assertThat(response.getList())
                .hasSize(1)
                .extracting("homeTeam.name", "awayTeam.name", "category")
                .containsExactly(
                        tuple(team2.getName(), team1.getName(), MatchCategory.FOOTBALL.name())
                );
    }

    @DisplayName("개별 경기 조회 성공")
    @Test
    void findOne_success() {
        // given
        Team home = createTeam(1, TeamCategory.FOOTBALL);
        Team away = createTeam(2, TeamCategory.FOOTBALL);
        Match match = createMatch(home, away, MatchCategory.FOOTBALL, LocalDateTime.now());

        // when
        MatchResponse response = matchReadService.findOne(match.getId());

        // then
        assertThat(response)
                .extracting("homeTeam.name", "awayTeam.name", "category")
                .containsExactly(home.getName(), away.getName(), MatchCategory.FOOTBALL.name());
    }
}