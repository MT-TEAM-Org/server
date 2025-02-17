package org.myteam.server.inquiry.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.repository.OrderType;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class InquiryServiceRequest extends PageInfoServiceRequest {
    @NotNull(message = "멤버 id는 필수입니다.")
    private UUID memberPublicId;

    @NotNull(message = "문의하기 정렬 타입은 필수입니다.")
    private InquiryOrderType orderType;

    private InquirySearchType searchType;

    private String content;

    @Builder
    public InquiryServiceRequest(UUID memberPublicId, InquiryOrderType orderType, InquirySearchType searchType, String content, int size, int page) {
        super(page, size);
        this.memberPublicId = memberPublicId;
        this.searchType = searchType;
        this.orderType = orderType;
        this.content = content;
    }

}
