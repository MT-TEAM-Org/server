package org.myteam.server.board.dto.reponse;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;

@Getter
@NoArgsConstructor
public class BoardDto {
    /**
     * 게시판 타입
     */
    private BoardType boardType;
    /**
     * 게시판 카테고리 타입
     */
    private CategoryType categoryType;
    /**
     * 게시글 ID
     */
    private Long id;
    /**
     * 게시글 제목
     */
    private String title;
    /**
     * 작성시 IP
     */
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
    private Integer commentCount;
    /**
     * 작성 일시
     */
    private LocalDateTime createdAt;
    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;

    public BoardDto(BoardType boardType, CategoryType categoryType, Long id, String title,
                    String createdIp, String thumbnail, UUID publicId, String nickname, Integer commentCount,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.boardType = boardType;
        this.categoryType = categoryType;
        this.id = id;
        this.title = title;
        this.createdIp = createdIp;
        this.thumbnail = thumbnail;
        this.publicId = publicId;
        this.nickname = nickname;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}