package org.myteam.server.inquiry.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class InquiryFindRequest extends PageInfoServiceRequest {

    @NotNull(message = "멤버 id는 필수입니다.")
    private UUID memberPublicId;

    @NotNull(message = "문의하기 정렬 타입은 필수입니다.")
    private InquiryOrderType orderType;

    private InquirySearchType searchType;

    private String search;

    @Builder
    public InquiryFindRequest(UUID memberPublicId, InquiryOrderType orderType, InquirySearchType searchType, String search, int page, int size) {
        super(page, size);
        this.memberPublicId = memberPublicId;
        this.orderType = orderType;
        this.searchType = searchType;
        this.search = search;
    }
}
