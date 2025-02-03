package org.myteam.server.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.myteam.server.inquiry.domain.Inquiry;

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

    // Entity -> DTO 변환 메서드
    public static InquiryResponse createInquiryResponse(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .content(inquiry.getContent())
                .memberNickname(inquiry.getMember().getNickname())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}