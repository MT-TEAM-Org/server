package org.myteam.server.match.matchPrediction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.matchPrediction.dto.service.response.MatchPredictionResponse;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;

class MatchPredictionReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MatchPredictionReadService matchPredictionReadService;

    private final long NON_EXISTS = 9999L;
    private Team home;
    private Team away;
    private Match match;
    private MatchPrediction matchPrediction;
    private Member member;

    @BeforeEach
    void setUp() {
        home = createTeam(1, TeamCategory.FOOTBALL);
        away = createTeam(2, TeamCategory.FOOTBALL);
        match = createMatch(home, away, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
        matchPrediction = createMatchPrediction(match, 1, 2);
        member = createMember(1);
    }

    @DisplayName("경기예측 현황을 조회한다.")
    @Test
    void findById() {
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);

        Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
        createMatchPrediction(match, 1, 2);

        assertThat(matchPredictionReadService.findOne(match.getId()))
                .extracting("home", "away")
                .contains(33, 67);
    }

    @Test
    @DisplayName("1. 경기 예측 데이터가 존재하면 조회 성공")
    void findOne_success_withPredictionMember() {
        // given
        createMatchPredictionMember(member, matchPrediction);
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(member.getPublicId());

        // when
        MatchPredictionResponse response = matchPredictionReadService.findOne(match.getId());

        // then
        assertThat(response)
                .extracting("home", "away", "isVote", "side")
                .contains(33, 67, true, Side.HOME);
    }

    @Test
    @DisplayName("2. 경기 예측 데이터는 있지만 회원이 예측한 기록이 없는 경우 → 추천 안함")
    void findOne_success_withoutPredictionMember() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(member.getPublicId());

        // when
        MatchPredictionResponse response = matchPredictionReadService.findOne(match.getId());

        // then
        assertThat(response)
                .extracting("home", "away", "isVote", "side")
                .contains(33, 67, false, null);
    }

    @Test
    @DisplayName("3. 로그인하지 않은 경우 → 추천 안함")
    void findOne_withoutLogin() {
        // given
        when(securityReadService.getAuthenticatedPublicId()).thenReturn(null);

        // when
        MatchPredictionResponse response = matchPredictionReadService.findOne(match.getId());

        // then
        assertThat(response)
                .extracting("home", "away", "isVote", "side")
                .contains(33, 67, false, null);
    }

    @Test
    @DisplayName("4. 존재하지 않는 경기 예측 조회 시 예외 발생")
    void findOne_matchPredictionNotFound() {
        // given
        Long wrongId = 9999L;

        // when & then
        assertThatThrownBy(() -> matchPredictionReadService.findOne(wrongId))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.MATCH_PREDICTION_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("5. findByIdLock: 존재하는 경우 정상 조회")
    void findByIdLock_success() {
        // when
        MatchPrediction found = matchPredictionReadService.findByIdLock(matchPrediction.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(matchPrediction.getId());
    }

    @Test
    @DisplayName("6. findByIdLock: 존재하지 않는 경우 예외 발생")
    void findByIdLock_notFound() {
        // when & then
        assertThatThrownBy(() -> matchPredictionReadService.findByIdLock(9999L))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.MATCH_PREDICTION_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("7. findById: 존재하는 경우 정상 조회")
    void findById_success() {
        // when
        MatchPrediction found = matchPredictionReadService.findById(matchPrediction.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(matchPrediction.getId());
    }

    @Test
    @DisplayName("8. findById: 존재하지 않는 경우 예외 발생")
    void findById_notFound() {
        // when & then
        assertThatThrownBy(() -> matchPredictionReadService.findById(NON_EXISTS))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.MATCH_PREDICTION_NOT_FOUND.getMsg());
    }

}