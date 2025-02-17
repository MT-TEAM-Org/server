package org.myteam.server.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.member.entity.Member;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class InquiryCommentResponse {

    private Long inquiryCommentId; // 문의내역 댓글 id
    private Long inquiryId; // 문의 내역 id
    private String createdIp; // 작성 ip
    private UUID publicId; // 작성자 id
    private String nickname; // 작성자 닉네임
    private String imageUrl; // 이미지 url
    private int recommendCount; // 추천 수
    private String comment; // 댓글 내용
    private LocalDateTime createDate; // 작성 일시

    @Builder
    public InquiryCommentResponse(Long inquiryCommentId, Long inquiryId, String createdIp,
                                  UUID publicId, String nickname, String imageUrl,
                                  int recommendCount, String comment, LocalDateTime createDate) {
        this.inquiryCommentId = inquiryCommentId;
        this.inquiryId = inquiryId;
        this.createdIp = createdIp;
        this.publicId = publicId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.recommendCount = recommendCount;
        this.comment = comment;
        this.createDate = createDate;
    }

    public static InquiryCommentResponse createResponse(InquiryComment inquiryComment, Member member) {
        return InquiryCommentResponse.builder()
                .inquiryCommentId(inquiryComment.getId())
                .inquiryId(inquiryComment.getInquiry().getId())
                .createdIp(inquiryComment.getCreatedIp())
                .publicId(member.getPublicId())
                .nickname(member.getNickname())
                .imageUrl(inquiryComment.getImageUrl())
                .comment(inquiryComment.getComment())
                .recommendCount(inquiryComment.getRecommendCount())
                .createDate(inquiryComment.getCreateDate())
                .build();
    }
}
