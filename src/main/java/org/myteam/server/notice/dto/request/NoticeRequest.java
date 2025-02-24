package org.myteam.server.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.notice.domain.NoticeSearchType;

public record NoticeRequest() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NoticeSaveRequest {
        @NotBlank(message = "제목을 입력해주세요")
        private String title;
        @NotBlank(message = "내용을 입력해주세요")
        private String content;
        private String imgUrl; // 썸네일 이미지
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NoticeSearchRequest extends PageInfoRequest {
        private NoticeSearchType searchType;
        private String search;

        public NoticeServiceRequest toServiceRequest() {
            return NoticeServiceRequest.builder()
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
    public static final class NoticeServiceRequest extends PageInfoServiceRequest {
        private NoticeSearchType searchType;
        private String search;

        @Builder
        public NoticeServiceRequest(NoticeSearchType searchType, String search, int size, int page) {
            super(page, size);
            this.searchType = searchType;
            this.search = search;
        }
    }
}
