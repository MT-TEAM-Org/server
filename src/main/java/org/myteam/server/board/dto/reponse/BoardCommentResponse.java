package org.myteam.server.board.dto.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.member.entity.Member;

@Getter
@NoArgsConstructor
public class BoardCommentResponse {
    /**
     * 게시판 댓글 id
     */
    private Long boardCommentId;
    /**
     * 게시판 id
     */
    private Long boardId;
    /**
     * 작성 ip
     */
    private String createdIp;
    /**
     * 작성자 id
     */
    private UUID publicId;
    /**
     * 작성자 닉네임
     */
    private String nickname;
    /**
     * 이미지
     */
    private String imageUrl;
    /**
     * 댓글 내용
     */
    private String comment;
    /**
     * 로그인한 사용자 게시글 댓글 추천 여부
     */
    @Setter
    @JsonProperty("isRecommended")
    private boolean isRecommended;
    /**
     * 추천 수
     */
    private int recommendCount;
    /**
     * 작성 일시
     */
    private LocalDateTime createDate;
    /**
     * 수정 일시
     */
    private LocalDateTime lastModifiedDate;
    /**
     * 대댓글 목록
     */
    @Setter
    private List<BoardReplyResponse> boardReplyList;

    @Builder
    public BoardCommentResponse(Long boardCommentId, Long boardId, String createdIp, UUID publicId, String nickname,
                                String imageUrl, String comment, int recommendCount,
                                LocalDateTime createDate,
                                LocalDateTime lastModifiedDate, boolean isRecommended) {
        this.boardCommentId = boardCommentId;
        this.boardId = boardId;
        this.createdIp = createdIp;
        this.publicId = publicId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.isRecommended = isRecommended;
        this.recommendCount = recommendCount;
        this.createDate = createDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public static BoardCommentResponse createResponse(BoardComment boardComment, Member member, boolean isRecommended) {
        return BoardCommentResponse.builder()
                .boardCommentId(boardComment.getId())
                .boardId(boardComment.getBoard().getId())
                .createdIp(boardComment.getCreatedIp())
                .publicId(member.getPublicId())
                .nickname(member.getNickname())
                .imageUrl(boardComment.getImageUrl())
                .comment(boardComment.getComment())
                .isRecommended(isRecommended)
                .recommendCount(boardComment.getRecommendCount())
                .createDate(boardComment.getCreateDate())
                .lastModifiedDate(boardComment.getLastModifiedDate())
                .build();
    }
}
