package org.myteam.server.mypage.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.comment.domain.CommentType;

@Getter
@NoArgsConstructor
public class PostResponse {
    /**
     * 댓글 타입
     */
    private CommentType commentType;
    /**
     * 게시글 id
     */
    private Long id;
    /**
     * 게시글 썸네일
     */
    private String thumbnail;
    /**
     * 게시글 제목
     */
    private String title;
    /**
     * 게시판 타입 (게시판일 경우만)
     */
    private BoardType boardType;
    /**
     * 카테고리 타입 (게시판일 경우만)
     */
    private CategoryType categoryType;
    /**
     * 게시글 작성시 IP
     */
    private String createdIp;
    /**
     * 게시글 작성자 id
     */
    private UUID publicId;
    /**
     * 게시글 작성자 닉네임
     */
    private String nickname;
    /**
     * 게시글 댓글수
     */
    private int commentCount;
    /**
     * 게시글 작성 일시
     */
    private LocalDateTime createDate;
    /**
     * 게시글 수정 일시
     */
    private LocalDateTime lastModifiedDate;
    /**
     * HOT 게시글 여부
     */
    @Setter
    @JsonProperty("isHot")
    private boolean isHot;

    public PostResponse(CommentType commentType, Long id, String thumbnail, String title, BoardType boardType,
                        CategoryType categoryType, String createdIp, UUID publicId, String nickname, int commentCount,
                        LocalDateTime createDate, LocalDateTime lastModifiedDate, boolean isHot) {
        this.commentType = commentType;
        this.id = id;
        this.thumbnail = thumbnail;
        this.title = title;
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.createdIp = createdIp;
        this.publicId = publicId;
        this.nickname = nickname;
        this.commentCount = commentCount;
        this.createDate = createDate;
        this.lastModifiedDate = lastModifiedDate;
        this.isHot = isHot;
    }
}
