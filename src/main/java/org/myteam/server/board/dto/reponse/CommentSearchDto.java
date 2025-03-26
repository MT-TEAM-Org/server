package org.myteam.server.board.dto.reponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentSearchDto {
    /**
     * 댓글 id
     */
    private Long commentId;
    /**
     * 댓글
     */
    private String comment;
    /**
     * 이미지
     */
    private String imageUrl;
}
