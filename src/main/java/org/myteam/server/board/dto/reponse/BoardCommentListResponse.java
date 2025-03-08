package org.myteam.server.board.dto.reponse;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class BoardCommentListResponse {

    private long total;

    @Setter
    private List<BoardCommentResponse> bestComment;

    private List<BoardCommentResponse> content;

    @Builder
    public BoardCommentListResponse(long total, List<BoardCommentResponse> list) {
        this.total = total;
        this.content = list;
    }

    public static BoardCommentListResponse createResponse(List<BoardCommentResponse> list) {
        return BoardCommentListResponse.builder()
                .total(list.size())
                .list(list)
                .build();
    }
}
