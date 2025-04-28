package org.myteam.server.improvement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.improvement.domain.ImprovementOrderType;
import org.myteam.server.improvement.domain.ImprovementSearchType;

public record ImprovementRequest() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ImprovementSaveRequest {
        @NotBlank(message = "제목을 입력해주세요")
        @Size(max = 30, message = "제목은 30자 이내로 입력해주세요")
        private String title;
        @NotBlank(message = "내용을 입력해주세요")
        private String content;
        private String imgUrl; // 썸네일 이미지
        private String link; // 출처 링크
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ImprovementSearchRequest extends PageInfoRequest {

        private ImprovementOrderType orderType;
        private ImprovementSearchType searchType;
        private String search;

        public ImprovementServiceRequest toServiceRequest() {
            return ImprovementServiceRequest.builder()
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
    public static final class ImprovementServiceRequest extends PageInfoServiceRequest {

        private ImprovementOrderType orderType;
        private ImprovementSearchType searchType;
        private String search;

        @Builder
        public ImprovementServiceRequest(ImprovementOrderType orderType, ImprovementSearchType searchType,
                                         String search, int size, int page) {
            super(page, size);
            this.orderType = orderType;
            this.searchType = searchType;
            this.search = search;
        }
    }
}
