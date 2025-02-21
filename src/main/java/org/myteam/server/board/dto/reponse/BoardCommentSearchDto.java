package org.myteam.server.board.dto.reponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardCommentSearchDto {
    /**
     * 댓글 검색 시 결과
     */
    private Long boardCommentId;
    /**
     * 댓글 검색 시 결과
     */
    private String comment;
}
