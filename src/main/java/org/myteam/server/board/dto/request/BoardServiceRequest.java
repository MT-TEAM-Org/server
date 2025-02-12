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
import org.myteam.server.global.page.request.PageInfoServiceRequest;

@Getter
@NoArgsConstructor
public class BoardServiceRequest extends PageInfoServiceRequest {
    @NotNull(message = "게시판 타입은 필수입니다.")
    private BoardType boardType;
    @NotNull(message = "카테고리 타입은 필수입니다.")
    private CategoryType categoryType;
    @NotNull(message = "게시판 정렬 타입은 필수입니다.")
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