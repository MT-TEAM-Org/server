package org.myteam.server.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.board.dto.reponse.BoardReplyResponse;
import org.myteam.server.global.domain.BaseTime;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.member.entity.Member;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class InquiryReplyResponse {

    /**
     * 문의사항 대댓글 id
     */
    private Long inquiryReplyId;
    /**
     * 문의사항 댓글 id
     */
    private Long inquiryCommentId;
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
    public InquiryReplyResponse(Long inquiryReplyId, Long inquiryCommentId, String createdIp, UUID publicId, String nickname,
                              String imageUrl, String comment, int recommendCount, UUID mentionedPublicId,
                              String mentionedNickname, LocalDateTime createDate, LocalDateTime lastModifiedDate) {
        this.inquiryReplyId = inquiryReplyId;
        this.inquiryCommentId = inquiryCommentId;
        this.createdIp = createdIp;
        this.publicId = publicId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.recommendCount = recommendCount;
        this.mentionedPublicId = mentionedPublicId;
        this.mentionedNickname = mentionedNickname;
        this.createDate = createDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public static InquiryReplyResponse createResponse(InquiryReply inquiryReply, Member member, Member mentionedMember) {
        return InquiryReplyResponse.builder()
                .inquiryReplyId(inquiryReply.getId())
                .inquiryCommentId(inquiryReply.getInquiryComment().getId())
                .createdIp(inquiryReply.getCreatedIp())
                .publicId(member.getPublicId())
                .nickname(member.getNickname())
                .imageUrl(inquiryReply.getImageUrl())
                .comment(inquiryReply.getComment())
                .recommendCount(inquiryReply.getRecommendCount())
                .mentionedPublicId(mentionedMember != null ? mentionedMember.getPublicId() : null)
                .mentionedNickname(mentionedMember != null ? mentionedMember.getNickname() : null)
                .createDate(inquiryReply.getCreateDate())
                .lastModifiedDate(inquiryReply.getLastModifiedDate())
                .build();
    }
}
