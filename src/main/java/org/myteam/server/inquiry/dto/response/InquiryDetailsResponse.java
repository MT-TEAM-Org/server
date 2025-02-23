package org.myteam.server.inquiry.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class InquiryDetailsResponse {

    private Long inquiryId; // 문의사항 id
    private UUID publicID; // 작성자 publicId
    private String nickname; // 작성자 nickname
    private String clientIp; // 작성자 IP
    private String content; // 게시글 내용
    private LocalDateTime createdAt; // 작성일시
    private int commentCount; // 댓글 수

    @Builder
    public InquiryDetailsResponse(Inquiry inquiry, InquiryCount inquiryCount) {
        this.inquiryId = inquiry.getId();
        this.publicID = inquiry.getMember().getPublicId();
        this.nickname = inquiry.getMember().getNickname();
        this.clientIp = inquiry.getClientIp();
        this.content = inquiry.getContent();
        this.createdAt = inquiry.getCreatedAt();
        this.commentCount = inquiryCount.getCommentCount();
    }

    public static InquiryDetailsResponse createResponse(Inquiry inquiry, InquiryCount inquiryCount) {
        return InquiryDetailsResponse.builder()
                .inquiry(inquiry)
                .inquiryCount(inquiryCount)
                .build();
    }
}
