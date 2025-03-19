package org.myteam.server.board.dto.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.util.ClientUtils;

@Getter
@Setter
public class BoardResponse {
    /**
     * 게시판 타입
     */
    private Category boardType;
    /**
     * 카테고리 타입
     */
    private CategoryType categoryType;
    /**
     * 게시글 id
     */
    private Long boardId;
    /**
     * 작성자 id
     */
    private UUID publicId;
    /**
     * 작성자 닉네임
     */
    private String nickname;
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
     * 썸네일 이미지
     */
    private String thumbnail;
    /**
     * 로그인한 사용자 게시글 추천 여부
     */
    @JsonProperty("isRecommended")
    private boolean isRecommended;
    /**
     * 추천 수
     */
    private Integer recommendCount;
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
    private LocalDateTime createDate;
    /**
     * 수정 일시
     */
    private LocalDateTime lastModifiedDate;

    @Builder
    public BoardResponse(Board board, BoardCount boardCount, boolean isRecommended) {
        this.boardType = board.getBoardType();
        this.categoryType = board.getCategoryType();
        this.boardId = board.getId();
        this.publicId = board.getMember().getPublicId();
        this.nickname = board.getMember().getNickname();
        this.clientIp = ClientUtils.maskIp(board.getCreatedIp());
        this.title = board.getTitle();
        this.content = board.getContent();
        this.link = board.getLink();
        this.thumbnail = board.getThumbnail();
        this.isRecommended = isRecommended;
        this.recommendCount = boardCount.getRecommendCount();
        this.commentCount = boardCount.getCommentCount();
        this.viewCount = boardCount.getViewCount();
        this.createDate = board.getCreateDate();
        this.lastModifiedDate = board.getLastModifiedDate();
    }

    public static BoardResponse createResponse(Board board, BoardCount boardCount, boolean isRecommended) {
        return BoardResponse.builder()
                .board(board)
                .boardCount(boardCount)
                .isRecommended(isRecommended)
                .build();
    }
}