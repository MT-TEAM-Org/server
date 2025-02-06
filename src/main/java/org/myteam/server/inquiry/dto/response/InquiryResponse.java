package org.myteam.server.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryAnswer;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponse {
    private Long id;
    private String content;
    private String memberNickname;
    private String clientIp;
    private LocalDateTime createdAt;
    private String answerContent;
    private LocalDateTime answeredAt;

    // Entity -> DTO 변환 메서드
    public static InquiryResponse createInquiryResponse(Inquiry inquiry) {
        InquiryAnswer inquiryAnswer = inquiry.getInquiryAnswer();

        return InquiryResponse.builder()
                .id(inquiry.getId())
                .content(inquiry.getContent())
                .memberNickname(inquiry.getMember().getNickname())
                .createdAt(inquiry.getCreatedAt())
                .answerContent(inquiryAnswer != null ? inquiryAnswer.getContent() : "답변 없음")
                .answeredAt(inquiryAnswer != null ? inquiryAnswer.getAnsweredAt() : null)
                .build();
    }
}