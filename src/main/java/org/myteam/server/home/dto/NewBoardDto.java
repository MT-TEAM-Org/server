package org.myteam.server.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;

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
    private Category boardType;
    /**
     * 게시판 카테고리 타입
     */
    private CategoryType categoryType;
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
    /**
     * New 여부
     */
    @Setter
    @JsonProperty("isNew")
    private boolean isNew;
    /**
     * 이미지 존재 여부
     */
    @Setter
    @JsonProperty("isImage")
    private boolean isImage;


    public NewBoardDto(Long id, Category boardType, CategoryType categoryType, String title, Integer commentCount,
                       boolean isHot, boolean isNew, boolean isImage) {
        this.id = id;
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.title = title;
        this.commentCount = commentCount;
        this.isHot = isHot;
        this.isNew = isNew;
        this.isImage = isImage;
    }
}
