package org.myteam.server.game.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.game.dto.response.GameEventDto;
import org.myteam.server.game.dto.response.GameEventListResponse;
import org.myteam.server.game.repository.GameQueryRepository;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameReadService {

    private final GameQueryRepository gameQueryRepository;

    public GameEventListResponse getGameEventList(PageInfoServiceRequest pageInfoServiceRequest) {
        Page<GameEventDto> gameEventList = gameQueryRepository.getGameEventList(
                pageInfoServiceRequest.toPageable()
        );
        return GameEventListResponse.createResponse(PageCustomResponse.of(gameEventList));
    }
}