package org.myteam.server.board.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.page.request.PageInfoRequest;

@Getter
@Setter
@NoArgsConstructor
public class BoardRequest extends PageInfoRequest {

    @NotNull(message = "게시판 타입은 필수입니다")
    private BoardType boardType;

    private CategoryType categoryType;

    private BoardOrderType orderType;

    @Builder
    public BoardRequest(BoardType boardType, CategoryType categoryType, BoardOrderType orderType) {
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.orderType = orderType;
    }

    public BoardServiceRequest toServiceRequest() {
        return BoardServiceRequest.builder()
                .boardType(boardType)
                .categoryType(categoryType)
                .orderType(orderType)
                .size(getSize())
                .page(getPage())
                .build();
    }
}