package org.myteam.server.mypage.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.response.PageCustomResponse;

@Getter
@NoArgsConstructor
public class MyCommentListResponse {
    /**
     * 내가 쓴 댓글 목록
     */
    private PageCustomResponse<MyCommentDto> list;

    @Builder
    public MyCommentListResponse(PageCustomResponse<MyCommentDto> list) {
        this.list = list;
    }

    public static MyCommentListResponse createResponse(PageCustomResponse<MyCommentDto> list) {
        return MyCommentListResponse.builder()
                .list(list)
                .build();
    }
}