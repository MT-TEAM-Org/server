package org.myteam.server.game.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.response.PageCustomResponse;

@Getter
@NoArgsConstructor
public class GameDiscountListResponse {

    private PageCustomResponse<GameDiscountDto> list;

    @Builder
    public GameDiscountListResponse(PageCustomResponse<GameDiscountDto> list) {
        this.list = list;
    }

    public static GameDiscountListResponse createResponse(PageCustomResponse<GameDiscountDto> list) {
        return GameDiscountListResponse.builder()
                .list(list)
                .build();
    }
}