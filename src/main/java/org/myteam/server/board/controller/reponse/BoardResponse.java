package org.myteam.server.board.controller.reponse;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.myteam.server.board.entity.Board;
import org.myteam.server.board.entity.BoardCount;

@Getter
@Setter
public class BoardResponse {
    /**
     * 카테고리 부모 id
     */
    private Long categoryParentId;
    /**
     * 카테고리 부모 명
     */
    private String categoryParentName;
    /**
     * 카테고리 id
     */
    private Long categoryId;
    /**
     * 카테고리명
     */
    private String categoryName;
    /**
     * 게시글 id
     */
    private Long boardId;
    /**
     * 작성자 id
     */
    private Long authorId;
    /**
     * 작성자 IP
     */
    private String clientIp;
    /**
     * 게시글 제목
     */
    private String title;
    /**
     * 게시글 내용
     */
    private String content;
    /**
     * 출처 링크
     */
    private String link;
    /**
     * 좋아요 수
     */
    private Integer likeCount;
    /**
     * 댓글 수
     */
    private Integer commentCount;
    /**
     * 조회 수
     */
    private Integer viewCount;
    /**
     * 작성 일시
     */
    private LocalDateTime createdAt;
    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;

    public BoardResponse(Board board, BoardCount boardCount) {
        this.boardId = board.getId();
        this.categoryParentId = board.getCategory().getCategoryParentId();
        this.categoryParentName = board.getCategory().getParentCategoryName();
        this.categoryId = board.getCategory().getId();
        this.categoryName = board.getCategory().getName();
        this.authorId = board.getMember().getId();
        this.clientIp = board.getCreatedIp();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.link = board.getLink();
        this.likeCount = boardCount.getLikeCount();
        this.commentCount = boardCount.getCommentCount();
        this.viewCount = boardCount.getViewCount();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
    }
}
