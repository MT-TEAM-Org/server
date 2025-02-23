package org.myteam.server.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.myteam.server.inquiry.domain.Inquiry;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponse {
    private Long id; // 문의내역 id
    private String content; // 문의내역 내용
    private String clientIp; // 쓴 사람 IP
    private LocalDateTime createdAt; // 쓴 시각
    private UUID publicId; // 쓴 사람 public ID
    private String nickname; // 쓴 사람 닉네임
    private String isAdminAnswered; // 관리자 답변
    private int commentCount; // 댓글 수

    // Entity -> DTO 변환 메서드
    public static InquiryResponse createInquiryResponse(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .content(inquiry.getContent())
                .createdAt(inquiry.getCreatedAt())
                .publicId(inquiry.getMember().getPublicId())
                .nickname(inquiry.getMember().getNickname())
                .isAdminAnswered(inquiry.isAdminAnswered() != true ? "접수완료" : "답변완료")
                .commentCount(inquiry.getInquiryCount().getCommentCount())
                .build();
    }
}