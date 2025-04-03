package org.myteam.server.inquiry.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.board.dto.reponse.CommentSearchDto;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.util.ClientUtils;

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
                    .clientIp(ClientUtils.maskIp(inquiry.getClientIp()))
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
        private PageCustomResponse<InquiryDto> list;

        @Builder
        public InquiriesListResponse(PageCustomResponse<InquiryDto> list) {
            this.list = list;
        }

        public static InquiriesListResponse createResponse(PageCustomResponse<InquiryDto> list) {
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
        /**
         * 이전글
         */
        private Long previousId;
        /**
         * 다음글
         */
        private Long nextId;


        @Builder
        public InquiryDetailsResponse(Inquiry inquiry, InquiryCount inquiryCount, Long previousId, Long nextId) {
            this.inquiryId = inquiry.getId();
            this.publicID = inquiry.getMember().getPublicId();
            this.nickname = inquiry.getMember().getNickname();
            this.clientIp = ClientUtils.maskIp(inquiry.getClientIp());
            this.content = inquiry.getContent();
            this.createdAt = inquiry.getCreatedAt();
            this.commentCount = inquiryCount.getCommentCount();
            this.previousId = previousId;
            this.nextId = nextId;
        }

        public static InquiryDetailsResponse createResponse(Inquiry inquiry, InquiryCount inquiryCount, Long previousId,
                                                            Long nextId) {
            return InquiryDetailsResponse.builder()
                    .inquiry(inquiry)
                    .inquiryCount(inquiryCount)
                    .previousId(previousId)
                    .nextId(nextId)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquiryDto {
        private Long id;
        private String content; // 문의내역 내용
        private String clientIp; // 쓴 사람 IP
        private LocalDateTime createdAt; // 쓴 시각
        private UUID publicId; // 쓴 사람 public ID
        private String nickname; // 쓴 사람 닉네임
        private String isAdminAnswered; // 관리자 답변
        private int commentCount; // 댓글 수

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private CommentSearchDto commentSearchList;
    }
}
