package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.member.service.MemberReadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InquiryReadService {
    private final MemberReadService memberReadService;
    private final InquiryRepository inquiryRepository;

    public PageCustomResponse<InquiryResponse> getInquiriesByMember(UUID memberPublicId, PageInfoRequest pageInfoRequest) {
        Member member = memberReadService.findById(memberPublicId);

        Pageable pageable = PageRequest.of(pageInfoRequest.getPage() - 1, pageInfoRequest.getSize());
        Page<Inquiry> inquiries = inquiryRepository.findByMember(member, pageable);
        Page<InquiryResponse> inquiryResponses = inquiries.map(InquiryResponse::createInquiryResponse);

        return PageCustomResponse.of(inquiryResponses);
    }
}
