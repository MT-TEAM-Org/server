package org.myteam.server.mypage.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.global.page.request.PageInfoServiceRequest;

@Getter
@NoArgsConstructor
public class MyCommentServiceRequest extends PageInfoServiceRequest {
    /**
     * 댓글 타입
     */
    private CommentType commentType;
    /**
     * 정렬 타입
     */
    private BoardOrderType orderType;
    /**
     * 검색어 타입 (제목, 내용, 제목+내용)
     */
    private BoardSearchType searchType;
    /**
     * 검색어
     */
    private String search;

    @Builder
    public MyCommentServiceRequest(CommentType commentType, BoardOrderType orderType, BoardSearchType searchType,
                                   String search, int size,
                                   int page) {
        super(page, size);
        this.commentType = commentType;
        this.orderType = orderType;
        this.searchType = searchType;
        this.search = search;
    }
}
