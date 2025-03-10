package org.myteam.server.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.BoardType;

@Getter
@NoArgsConstructor
public class HotBoardDto {
    /**
     * 순위
     */
    @Setter
    private Integer rank;
    /**
     * 게시판 타입
     */
    private BoardType boardType;
    /**
     * 게시글 ID
     */
    private Long id;
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

    public HotBoardDto(Integer rank, BoardType boardType, Long id, String title, Integer commentCount, boolean isHot) {
        this.rank = rank;
        this.boardType = boardType;
        this.id = id;
        this.title = title;
        this.commentCount = commentCount;
        this.isHot = isHot;
    }
}
