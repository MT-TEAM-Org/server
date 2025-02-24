package org.myteam.server.board.dto.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.member.entity.Member;

@Getter
@NoArgsConstructor
public class BoardReplyResponse {
    /**
     * 게시판 대댓글 id
     */
    private Long boardReplyId;
    /**
     * 게시판 댓글 id
     */
    private Long boardCommentId;
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
     * 로그인한 사용자 게시글 대댓글 추천 여부
     */
    @Setter
    @JsonProperty("isRecommended")
    private boolean isRecommended;
    /**
     * 추천 수
     */
    private int recommendCount;
    /**
     * 멘션 당한 사용자 id
     */
    private UUID mentionedPublicId;
    /**
     * 멘션 당한 사용자 닉네임
     */
    private String mentionedNickname;
    /**
     * 작성 일시
     */
    private LocalDateTime createDate;
    /**
     * 수정 일시
     */
    private LocalDateTime lastModifiedDate;

    @Builder
    public BoardReplyResponse(Long boardCommentId, Long boardReplyId, String createdIp, UUID publicId, String nickname,
                              String imageUrl, String comment, boolean isRecommended, int recommendCount,
                              UUID mentionedPublicId,
                              String mentionedNickname, LocalDateTime createDate, LocalDateTime lastModifiedDate) {
        this.boardCommentId = boardCommentId;
        this.boardReplyId = boardReplyId;
        this.createdIp = createdIp;
        this.publicId = publicId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.isRecommended = isRecommended;
        this.recommendCount = recommendCount;
        this.mentionedPublicId = mentionedPublicId;
        this.mentionedNickname = mentionedNickname;
        this.createDate = createDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public static BoardReplyResponse createResponse(BoardReply boardReply, Member member, Member mentionedMember,
                                                    boolean isRecommended) {
        return BoardReplyResponse.builder()
                .boardCommentId(boardReply.getBoardComment().getId())
                .boardReplyId(boardReply.getId())
                .createdIp(boardReply.getCreatedIp())
                .publicId(member.getPublicId())
                .nickname(member.getNickname())
                .imageUrl(boardReply.getImageUrl())
                .comment(boardReply.getComment())
                .isRecommended(isRecommended)
                .recommendCount(boardReply.getRecommendCount())
                .mentionedPublicId(mentionedMember != null ? mentionedMember.getPublicId() : null)
                .mentionedNickname(mentionedMember != null ? mentionedMember.getNickname() : null)
                .createDate(boardReply.getCreateDate())
                .lastModifiedDate(boardReply.getLastModifiedDate())
                .build();
    }
}