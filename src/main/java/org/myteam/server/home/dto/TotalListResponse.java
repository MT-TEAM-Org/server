package org.myteam.server.home.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.myteam.server.global.page.response.PageCustomResponse;

@Getter
@NoArgsConstructor
public class TotalListResponse<T> {
    private PageCustomResponse<T> list;

    public TotalListResponse(PageCustomResponse<T> list) {
        this.list = list;
    }

    public static <T> TotalListResponse<T> createResponse(PageCustomResponse<T> list) {
        return new TotalListResponse<>(list);
    }
}
