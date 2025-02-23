package org.myteam.server.inquiry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InquiryCommentListResponse {

    private long total;
    private List<InquiryCommentResponse> content;

    @Builder
    public InquiryCommentListResponse(long total, List<InquiryCommentResponse> list) {
        this.total = total;
        this.content = list;
    }

    public static InquiryCommentListResponse createResponse(List<InquiryCommentResponse> list) {
        return InquiryCommentListResponse.builder()
                .total(list.size())
                .list(list)
                .build();
    }
}
