package org.myteam.server.match.matchPrediction.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.TestContainerSupport;
import org.myteam.server.match.matchPrediction.dto.service.response.MatchPredictionResponse;

public class MatchPredictionResponseTest extends TestContainerSupport {

    @DisplayName("경기예측 현황을 퍼센트로 바꾼다.")
    @Test
    void percent() {
        MatchPredictionResponse matchPredictionResponse1 = MatchPredictionResponse.builder()
                .matchId(1L)
                .id(1L)
                .home(1)
                .away(0)
                .build();

        MatchPredictionResponse matchPredictionResponse2 = MatchPredictionResponse.builder()
                .matchId(1L)
                .id(1L)
                .home(1)
                .away(1)
                .build();

        assertAll(
                () -> assertThat(matchPredictionResponse1).extracting("matchId", "id", "homePercent", "awayPercent")
                        .contains(1L, 1L, 100, 0),
                () -> assertThat(matchPredictionResponse2).extracting("matchId", "id", "homePercent", "awayPercent")
                        .contains(1L, 1L, 50, 50)
        );
    }
}
