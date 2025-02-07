package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.repository.InquiryQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InquiryReadService {
    private final InquiryQueryRepository inquiryQueryRepository;

    /**
     * 검색 + 정렬 기능
     * @param inquirySearchRequest
     * @return
     */
    public PageCustomResponse<InquiryResponse> getInquiriesByMember(InquirySearchRequest inquirySearchRequest) {
        Page<InquiryResponse> inquiryResponses = inquiryQueryRepository.getInquiryList(
                inquirySearchRequest.getMemberPublicId(),
                inquirySearchRequest.getOrderType(),
                inquirySearchRequest.getSearchType(),
                inquirySearchRequest.getKeyword(),
                inquirySearchRequest.toPageable()
        );

        return PageCustomResponse.of(inquiryResponses);
    }
}
