package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.request.InquiryServiceRequest;
import org.myteam.server.inquiry.dto.response.InquiriesListResponse;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.repository.InquiryQueryRepository;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.dto.request.InquiryFindRequest;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.repository.InquiryQueryRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InquiryReadService {
    private final InquiryQueryRepository inquiryQueryRepository;
    private final InquiryRepository inquiryRepository;
    private final SecurityReadService securityReadService;

    /**
     * 검색 + 정렬 기능
     * @param inquirySearchRequest
     * @return
     */
    public InquiriesListResponse getInquiriesByMember(InquiryFindRequest inquirySearchRequest) {
        log.info("내 문의내역 조회: {}", inquirySearchRequest.getMemberPublicId());

        Page<InquiryResponse> inquiryResponses = inquiryQueryRepository.getInquiryList(
                inquirySearchRequest.getMemberPublicId(),
                inquirySearchRequest.getOrderType(),
                inquirySearchRequest.getSearchType(),
                inquirySearchRequest.getKeyword(),
                inquirySearchRequest.toPageable()
        );

        return InquiriesListResponse.createResponse(PageCustomResponse.of(inquiryResponses));
    }

    public InquiriesListResponse getInquiriesByMember(InquiryServiceRequest inquiryServiceRequest) {
        Member member = securityReadService.getMember();
        log.info("내 문의내역 조회: {}", member.getPublicId());

        Page<InquiryResponse> inquiryResponses = inquiryQueryRepository.getInquiryList(
                member.getPublicId(),
                inquiryServiceRequest.getOrderType(),
                inquiryServiceRequest.getSearchType(),
                inquiryServiceRequest.getContent(),
                inquiryServiceRequest.toPageable()
        );

        return InquiriesListResponse.createResponse(PageCustomResponse.of(inquiryResponses));
    }

    /**
     * 내 문의하기 수
     * @param memberPublicId
     * @return
     */
    public int getInquiriesCountByMember(UUID memberPublicId) {
        return inquiryQueryRepository.getMyInquires(memberPublicId);
    }

    /**
     * 문의 내역 상세 조회
     * @TODO: 리턴 타입 다시 살펴보기
     */
    public InquiryResponse getInquiryById(final Long inquiryId) {
        Inquiry inquiry = findInquiryById(inquiryId);

        return InquiryResponse.createInquiryResponse(inquiry);
    }

    public Inquiry findInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.INQUIRY_NOT_FOUND));
        return inquiry;
    }
}