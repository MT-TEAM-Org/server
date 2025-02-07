package org.myteam.server.board.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.page.request.PageInfoServiceRequest;

@Getter
@NoArgsConstructor
public class BoardServiceRequest extends PageInfoServiceRequest {
    private BoardType boardType;
    private CategoryType categoryType;
    private BoardOrderType orderType;
    private BoardSearchType searchType;
    private String search;

    @Builder
    public BoardServiceRequest(BoardType boardType, CategoryType categoryType, BoardOrderType orderType,
                               BoardSearchType searchType, String search, int size, int page) {
        super(page, size);
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.orderType = orderType;
        this.searchType = searchType;
        this.search = search;
    }
}