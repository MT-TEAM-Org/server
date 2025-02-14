package org.myteam.server.match.matchPrediction.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.MatchPredictionServiceRequest;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.springframework.beans.factory.annotation.Autowired;

class MatchPredictionServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchPredictionService matchPredictionService;

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
					MatchPredictionServiceRequest request = MatchPredictionServiceRequest.builder()
						.matchPredictionId(matchPrediction.getId())
						.side(Side.HOME)
						.build();

					matchPredictionService.update(request);
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

}
