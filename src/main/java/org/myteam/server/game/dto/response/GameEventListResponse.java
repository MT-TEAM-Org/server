package org.myteam.server.game.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.response.PageCustomResponse;

@Getter
@NoArgsConstructor
public class GameEventListResponse {

    private PageCustomResponse<GameEventDto> list;

    @Builder
    public GameEventListResponse(PageCustomResponse<GameEventDto> list) {
        this.list = list;
    }

    public static GameEventListResponse createResponse(PageCustomResponse<GameEventDto> list) {
        return GameEventListResponse.builder()
                .list(list)
                .build();
    }
}