package org.myteam.server.board.dto.reponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.util.ClientUtils;

@Getter
@NoArgsConstructor
public class BoardDto {
    /**
     * 게시판 타입
     */
    private Category boardType;
    /**
     * 게시판 카테고리 타입
     */
    private CategoryType categoryType;
    /**
     * 게시글 ID
     */
    private Long id;
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
     * 게시글 제목
     */
    private String title;
    /**
     * 작성시 IP
     */
    @Setter
    private String createdIp;
    /**
     * 썸네일
     */
    private String thumbnail;
    /**
     * 작성자 ID
     */
    private UUID publicId;
    /**
     * 작성자 닉네임
     */
    private String nickname;
    /**
     * 댓글 수
     */
    @Setter
    private Integer commentCount;
    /**
     * 추천 수
     */
    @Setter
    private Integer recommendCount;
    /**
     * 작성 일시
     */
    private LocalDateTime createdAt;
    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
    /**
     * 댓글 검색 시 결과
     */
    @Setter
    @JsonInclude(Include.NON_EMPTY)
    private CommentSearchDto boardCommentSearchList;

    public BoardDto(Category boardType, CategoryType categoryType, Long id, boolean isHot, boolean isNew, String title,
                    String createdIp, String thumbnail, UUID publicId, String nickname, Integer commentCount,
                    Integer recommendCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.id = id;
        this.isHot = isHot;
        this.isNew = isNew;
        this.title = title;
        this.createdIp = ClientUtils.maskIp(createdIp);
        this.thumbnail = thumbnail;
        this.publicId = publicId;
        this.nickname = nickname;
        this.commentCount = commentCount;
        this.recommendCount = recommendCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}