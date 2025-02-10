package org.myteam.server.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.response.PageCustomResponse;

@Getter
@NoArgsConstructor
public class InquiriesListResponse {
    private PageCustomResponse<InquiryResponse> list;

    @Builder
    public InquiriesListResponse(PageCustomResponse<InquiryResponse> list) {
        this.list = list;
    }

    public static InquiriesListResponse createResponse(PageCustomResponse<InquiryResponse> list) {
        return InquiriesListResponse.builder()
                .list(list)
                .build();
    }
}
