package org.myteam.server.match.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.TestContainerSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.client.GoogleFeignClient;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.dto.client.reponse.GoogleYoutubeResponse;
import org.myteam.server.match.match.dto.service.response.MatchEsportsScheduleResponse;
import org.myteam.server.match.match.dto.service.response.MatchEsportsYoutubeResponse;
import org.myteam.server.match.match.dto.service.response.MatchResponse;
import org.myteam.server.match.match.dto.service.response.MatchScheduleListResponse;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MatchYoutubeServiceTest extends IntegrationTestSupport {

    @Autowired
    private MatchYoutubeService matchYoutubeService;

    @MockBean
    private GoogleFeignClient googleFeignClient;

    @Test
    @DisplayName("1. Redis에 videoId가 없으면 Google API 호출하고 Redis에 저장 후 반환한다")
    void getVideoId_whenCacheMiss_thenCallGoogleApiAndSave() {
        // given
        String expectedVideoId = "testVideoId";

        GoogleYoutubeResponse.Id id = new GoogleYoutubeResponse.Id();
        ReflectionTestUtils.setField(id, "videoId", expectedVideoId);

        GoogleYoutubeResponse.Item item = GoogleYoutubeResponse.Item.builder()
                .id(id)
                .build();

        GoogleYoutubeResponse response = GoogleYoutubeResponse.builder()
                .items(List.of(item))
                .build();

        given(redisService.getEsportsYoutubeVideoId()).willReturn(null);
        given(googleFeignClient.searchLiveVideos(any(), any(), any(), any(), any()))
                .willReturn(response);

        // when
        String videoId = matchYoutubeService.getVideoId();

        // then
        assertThat(videoId).isEqualTo(expectedVideoId);
        verify(redisService).putEsportsYoutubeVideoId(expectedVideoId);
    }

    @Test
    @DisplayName("2. Redis에 videoId가 있으면 Google API 호출 없이 바로 반환한다")
    void getVideoId_whenCacheHit_thenReturnFromCache() {
        // given
        String cachedVideoId = "cachedVideoId";
        given(redisService.getEsportsYoutubeVideoId()).willReturn(cachedVideoId);

        // when
        String videoId = matchYoutubeService.getVideoId();

        // then
        assertThat(videoId).isEqualTo(cachedVideoId);
        verify(googleFeignClient, never()).searchLiveVideos(any(), any(), any(), any(), any());
        verify(redisService, never()).putEsportsYoutubeVideoId(any());
    }

    @Test
    @DisplayName("3. Redis에 없고 Google API에 live 영상 없으면 API_SERVER_ERROR 예외 발생")
    void getVideoId_whenGoogleApiNoLive_thenThrowApiServerError() {
        // given
        GoogleYoutubeResponse response = GoogleYoutubeResponse.builder()
                .items(Collections.emptyList())
                .build();

        given(redisService.getEsportsYoutubeVideoId()).willReturn(null);
        given(googleFeignClient.searchLiveVideos(any(), any(), any(), any(), any()))
                .willReturn(response);

        // when & then
        assertThatThrownBy(() -> matchYoutubeService.getVideoId())
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.API_SERVER_ERROR.getMsg());

        verify(redisService, never()).putEsportsYoutubeVideoId(any());
    }

    @Test
    @DisplayName("4. Redis에 없고 Google API에서 videoId가 null이면 API_SERVER_ERROR 예외 발생")
    void getVideoId_whenGoogleApiReturnsNullVideoId_thenThrowApiServerError() {
        // given
        GoogleYoutubeResponse.Id id = new GoogleYoutubeResponse.Id(); // videoId는 null
        GoogleYoutubeResponse.Item item = GoogleYoutubeResponse.Item.builder()
                .id(id)
                .build();
        GoogleYoutubeResponse response = GoogleYoutubeResponse.builder()
                .items(List.of(item))
                .build();

        given(redisService.getEsportsYoutubeVideoId()).willReturn(null);
        given(googleFeignClient.searchLiveVideos(any(), any(), any(), any(), any()))
                .willReturn(response);

        // when & then
        assertThatThrownBy(() -> matchYoutubeService.getVideoId())
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.API_SERVER_ERROR.getMsg());

        verify(redisService, never()).putEsportsYoutubeVideoId(any());
    }
}