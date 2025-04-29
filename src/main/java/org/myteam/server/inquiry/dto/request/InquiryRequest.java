package org.myteam.server.inquiry.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;

public record InquiryRequest() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquirySaveRequest {
        private String email;

        @NotNull(message = "문의 내용이 없으면 안됩니다.")
        @Size(max = 400, message = "문의 내용은 400자 이내로 입력해주세요")
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquirySearchRequest extends PageInfoRequest {

        private InquiryOrderType orderType;
        private InquirySearchType searchType;
        private String search;


        public InquiryServiceRequest toServiceRequest() {
            return InquiryServiceRequest.builder()
                    .orderType(orderType)
                    .searchType(searchType)
                    .search(search)
                    .size(getSize())
                    .page(getPage())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquiryServiceRequest extends PageInfoServiceRequest {

        @NotNull(message = "문의하기 정렬 타입은 필수입니다.")
        private InquiryOrderType orderType;

        private InquirySearchType searchType;

        private String search;

        @Builder
        public InquiryServiceRequest(InquiryOrderType orderType, InquirySearchType searchType, String search, int size,
                                     int page) {
            super(page, size);
            this.searchType = searchType;
            this.orderType = orderType;
            this.search = search;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquiryFindRequest extends PageInfoServiceRequest {

        @NotNull(message = "문의하기 정렬 타입은 필수입니다.")
        private InquiryOrderType orderType;

        private InquirySearchType searchType;

        private String search;

        @Builder
        public InquiryFindRequest(InquiryOrderType orderType, InquirySearchType searchType, String search, int page,
                                  int size) {
            super(page, size);
            this.orderType = orderType;
            this.searchType = searchType;
            this.search = search;
        }
    }
}
