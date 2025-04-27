package org.myteam.server.game.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.common.certification.domain.CertificationCode;
import org.myteam.server.common.certification.mail.strategy.CertifyMailStrategy;
import org.myteam.server.common.certification.mail.util.CertifyStorage;
import org.myteam.server.game.dto.response.GameEventDto;
import org.myteam.server.game.dto.response.GameEventListResponse;
import org.myteam.server.game.repository.GameQueryRepository;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GameReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private GameReadService gameReadService;
    @MockBean
    private GameQueryRepository gameQueryRepository;

    @Test
    @DisplayName("게임 이벤트 리스트 조회 성공")
    void getGameEventList_success() {
        // given
        PageInfoServiceRequest pageInfoServiceRequest = new PageInfoServiceRequest(1, 10);

        List<GameEventDto> content = List.of(
                new GameEventDto(1L, "null", "Team A", "Team A description",
                        LocalDateTime.now().plusMinutes(1L).toString(), "test.co.kr", LocalDateTime.now()),
                new GameEventDto(2L, "null", "Team B", "Team B description",
                        LocalDateTime.now().plusDays(1L).toString(), "test.co.kr", LocalDateTime.now())
        );
        Page<GameEventDto> gameEventPage = new PageImpl<>(content, pageInfoServiceRequest.toPageable(), content.size());
        when(gameQueryRepository.getGameEventList(pageInfoServiceRequest.toPageable()))
                .thenReturn(gameEventPage);

        // when
        GameEventListResponse result = gameReadService.getGameEventList(pageInfoServiceRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getList().getContent()).hasSize(2);
        assertThat(result.getList().getContent().get(0).getTitle()).isEqualTo("Team A");
    }

    @Test
    @DisplayName("게임 이벤트 리스트가 비어있는 경우")
    void getGameEventList_empty() {
        // given
        PageInfoServiceRequest pageInfoServiceRequest = new PageInfoServiceRequest(1, 10);

        Page<GameEventDto> emptyPage = Page.empty(pageInfoServiceRequest.toPageable());

        when(gameQueryRepository.getGameEventList(pageInfoServiceRequest.toPageable()))
                .thenReturn(emptyPage);

        // when
        GameEventListResponse result = gameReadService.getGameEventList(pageInfoServiceRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getList().getContent()).isEmpty();
    }
}