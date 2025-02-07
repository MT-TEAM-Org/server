package org.myteam.server.board.dto.reponse;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.response.PageCustomResponse;

@Getter
@NoArgsConstructor
public class BoardListResponse {

    private PageCustomResponse<BoardDto> list;

    @Builder
    public BoardListResponse(PageCustomResponse<BoardDto> list) {
        this.list = list;
    }

    public static BoardListResponse createResponse(PageCustomResponse<BoardDto> list) {
        return BoardListResponse.builder()
                .list(list)
                .build();
    }
}