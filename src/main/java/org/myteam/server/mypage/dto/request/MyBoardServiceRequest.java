package org.myteam.server.mypage.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.page.request.PageInfoServiceRequest;

@Getter
@NoArgsConstructor
public class MyBoardServiceRequest extends PageInfoServiceRequest {
    private BoardOrderType orderType;
    private BoardSearchType searchType;
    private String search;

    @Builder
    public MyBoardServiceRequest(BoardOrderType orderType, BoardSearchType searchType, String search, int size,
                                 int page) {
        super(page, size);
        this.orderType = orderType;
        this.searchType = searchType;
        this.search = search;
    }
}