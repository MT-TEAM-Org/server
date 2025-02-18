package org.myteam.server.inquiry.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class InquirySearchRequest extends PageInfoRequest {

    private InquiryOrderType orderType;

    private InquirySearchType searchType;

    private String keyword;

    @Builder
    public InquirySearchRequest(InquiryOrderType orderType, InquirySearchType searchType, String keyword, int page, int size) {
        super(page, size);
        this.orderType = orderType;
        this.searchType = searchType;
        this.keyword = keyword;
    }

    public InquiryServiceRequest toServiceRequest() {
        return InquiryServiceRequest.builder()
                .orderType(orderType)
                .searchType(searchType)
                .content(keyword)
                .size(getSize())
                .page(getPage())
                .build();
    }
}