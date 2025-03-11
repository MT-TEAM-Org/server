package org.myteam.server.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.BoardType;

@Getter
@NoArgsConstructor
public class NewBoardDto {
    /**
     * 게시글 ID
     */
    private Long id;
    /**
     * 게시판 타입
     */
    private BoardType boardType;
    /**
     * 게시글 제목
     */
    private String title;
    /**
     * 게시글 댓글수
     */
    private Integer commentCount;
    /**
     * Hot 여부
     */
    @Setter
    @JsonProperty("isHot")
    private boolean isHot;

    public NewBoardDto(Long id, BoardType boardType, String title, Integer commentCount, boolean isHot) {
        this.id = id;
        this.boardType = boardType;
        this.title = title;
        this.commentCount = commentCount;
        this.isHot = isHot;
    }
}
