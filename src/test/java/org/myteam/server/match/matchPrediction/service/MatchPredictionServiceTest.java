package org.myteam.server.match.matchPrediction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.MatchPredictionServiceRequest;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.matchPredictionMember.service.MatchPredictionMemberReadService;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class MatchPredictionServiceTest extends IntegrationTestSupport {

    @Autowired
    private MatchPredictionService matchPredictionService;
    @MockBean
    private MatchPredictionMemberReadService matchPredictionMemberReadService;

    private Member member;
    private Match match;
    private MatchPrediction matchPrediction;

    @BeforeEach
    void setUp() {
        member = createMember(1);
        Team home = createTeam(1, TeamCategory.FOOTBALL);
        Team away = createTeam(2, TeamCategory.FOOTBALL);
        match = createMatch(home, away, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
        matchPrediction = createMatchPrediction(match, 5, 5);
    }

    @DisplayName("경기예측을 저장한다. 동시성 테스트")
    @Test
    void findById() throws ExecutionException, InterruptedException {
        Team team1 = Team.builder()
                .name("테스트팀1")
                .logo("www.test.com")
                .category(TeamCategory.FOOTBALL)
                .build();

        Team team2 = Team.builder()
                .name("테스트팀1")
                .logo("www.test.com")
                .category(TeamCategory.FOOTBALL)
                .build();

        Match match = Match.builder()
                .homeTeam(team1)
                .awayTeam(team2)
                .category(MatchCategory.FOOTBALL)
                .startTime(LocalDate.now().atStartOfDay())
                .build();

        MatchPrediction matchPrediction = MatchPrediction.builder()
                .match(match)
                .home(0)
                .away(0)
                .build();

        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(25);

        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        executorService.submit(() -> {
            teamRepository.save(team1);
            teamRepository.save(team2);
            matchRepository.save(match);
            matchPredictionRepository.save(matchPrediction);
        }).get();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    matchPredictionService.addCount(matchPrediction.getId(), Side.HOME);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        assertThat(matchPredictionRepository.findById(matchPrediction.getId()).get())
                .extracting("home", "away")
                .contains(50, 0);
    }

    @Test
    @DisplayName("1. 예측이 존재하는 경우 update 성공")
    void update_success() {
        // given
        MatchPredictionServiceRequest request = new MatchPredictionServiceRequest(matchPrediction.getId(), Side.HOME);
        when(securityReadService.getMember()).thenReturn(member);

        // when
        Long resultId = matchPredictionService.update(request);

        // then
        assertThat(resultId).isEqualTo(matchPrediction.getId());
    }

    @Test
    @DisplayName("2. 예측 기록 검증 실패시 예외 발생")
    void update_fail_when_alreadyPredicted() {
        // given
        MatchPredictionServiceRequest request = new MatchPredictionServiceRequest(matchPrediction.getId(), Side.AWAY);
        when(securityReadService.getMember()).thenReturn(member);

        // ✅ confirmExistMember 호출 시 PlayHiveException 던지도록 세팅
        doThrow(new PlayHiveException(ErrorCode.ALREADY_MEMBER_MATCH_PREDICTION))
                .when(matchPredictionMemberReadService)
                .confirmExistMember(matchPrediction.getId(), member.getPublicId());

        // when & then
        assertThatThrownBy(() -> matchPredictionService.update(request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.ALREADY_MEMBER_MATCH_PREDICTION.getMsg());
    }

    @Test
    @DisplayName("3. addCount 정상 동작")
    void addCount_success() {
        // when
        MatchPrediction result = matchPredictionService.addCount(matchPrediction.getId(), Side.HOME);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(matchPrediction.getId());
    }

}