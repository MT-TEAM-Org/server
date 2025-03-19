package org.myteam.server.mypage.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.comment.dto.response.CommentResponse.CommentSaveResponse;

@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class MyCommentDto {
    /**
     * 게시글 정보
     */
    @Setter
    private PostResponse postResponse;
    /**
     * 댓글 정보
     */
    @Setter
    private CommentSaveResponse commentResponse;

    public MyCommentDto(PostResponse postResponse, CommentSaveResponse commentResponse) {
        this.postResponse = postResponse;
        this.commentResponse = commentResponse;
    }
}
