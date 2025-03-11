package org.myteam.server.inquiry.dto.response;

import lombok.*;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.util.ClientUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public record InquiryResponse() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquirySaveResponse {
        private Long id; // 문의내역 id
        private String content; // 문의내역 내용
        private String clientIp; // 쓴 사람 IP
        private LocalDateTime createdAt; // 쓴 시각
        private UUID publicId; // 쓴 사람 public ID
        private String nickname; // 쓴 사람 닉네임
        private String isAdminAnswered; // 관리자 답변
        private int commentCount; // 댓글 수

        // Entity -> DTO 변환 메서드
        public static InquirySaveResponse createInquiryResponse(Inquiry inquiry) {
            return InquirySaveResponse.builder()
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

    @Data
    @Builder
    @NoArgsConstructor
    public static final class InquiriesListResponse {
        private PageCustomResponse<InquirySaveResponse> list;

        @Builder
        public InquiriesListResponse(PageCustomResponse<InquirySaveResponse> list) {
            this.list = list;
        }

        public static InquiriesListResponse createResponse(PageCustomResponse<InquirySaveResponse> list) {
            return InquiriesListResponse.builder()
                    .list(list)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquiryDetailsResponse {

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
            this.clientIp = ClientUtils.maskIp(inquiry.getClientIp());
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
}
