package org.myteam.server.board.dto.reponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardCommentSearchDto {
    /**
     * 댓글 id
     */
    private Long boardCommentId;
    /**
     * 댓글
     */
    private String comment;
    /**
     * 이미지
     */
    private String imageUrl;
}
