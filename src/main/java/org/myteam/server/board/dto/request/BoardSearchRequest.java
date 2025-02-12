package org.myteam.server.board.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.page.request.PageInfoRequest;

@Getter
@Setter
@NoArgsConstructor
public class BoardSearchRequest extends PageInfoRequest {

    @NotNull(message = "게시판 타입은 필수입니다")
    private BoardType boardType;

    private CategoryType categoryType;

    private BoardOrderType orderType;

    /**
     * 검색어 타입 (제목, 내용, 제목+내용,
     */
    private BoardSearchType searchType;
    /**
     * 검색어
     */
    private String search;

    @Builder
    public BoardSearchRequest(BoardType boardType, CategoryType categoryType, BoardOrderType orderType,
                              BoardSearchType searchType, String search) {
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.orderType = orderType;
        this.searchType = searchType;
        this.search = search;
    }

    public BoardServiceRequest toServiceRequest() {
        return BoardServiceRequest.builder()
                .boardType(boardType)
                .categoryType(categoryType)
                .orderType(orderType)
                .searchType(searchType)
                .search(search)
                .size(getSize())
                .page(getPage())
                .build();
    }
}