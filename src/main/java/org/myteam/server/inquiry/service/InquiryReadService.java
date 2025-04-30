package org.myteam.server.inquiry.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.aop.CountView;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.dto.request.InquiryRequest.InquiryServiceRequest;
import org.myteam.server.inquiry.dto.response.InquiryResponse.InquiriesListResponse;
import org.myteam.server.inquiry.dto.response.InquiryResponse.InquiryDetailsResponse;
import org.myteam.server.inquiry.dto.response.InquiryResponse.InquiryDto;
import org.myteam.server.inquiry.repository.InquiryQueryRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.util.ClientUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InquiryReadService {
    private final InquiryQueryRepository inquiryQueryRepository;
    private final InquiryRepository inquiryRepository;
    private final SecurityReadService securityReadService;
    private final RedisCountService redisCountService;

    /**
     * 검색 + 정렬 기능
     *
     * @param inquiryServiceRequest
     * @return
     */
    public InquiriesListResponse getInquiriesByMember(InquiryServiceRequest inquiryServiceRequest) {
        Member member = securityReadService.getMember();
        log.info("내 문의내역 조회: {} 요청", member.getPublicId());

        Page<InquiryDto> inquiryResponses = null;
        if (member.isAdmin()) {
            inquiryResponses = inquiryQueryRepository.getInquiryList(
                    null,
                    inquiryServiceRequest.getOrderType(),
                    inquiryServiceRequest.getSearchType(),
                    inquiryServiceRequest.getSearch(),
                    inquiryServiceRequest.toPageable()
            );
        } else {
            inquiryResponses = inquiryQueryRepository.getInquiryList(
                    member.getPublicId(),
                    inquiryServiceRequest.getOrderType(),
                    inquiryServiceRequest.getSearchType(),
                    inquiryServiceRequest.getSearch(),
                    inquiryServiceRequest.toPageable()
            );
        }

        log.info("내 문의내역: {} 조회 성공", member.getPublicId());
        inquiryResponses.getContent().forEach(response -> {
            log.info("ip: {}, maskedIp:{}", response.getClientIp(), ClientUtils.maskIp(response.getClientIp()));
            response.setClientIp(ClientUtils.maskIp(response.getClientIp()));
        });

        return InquiriesListResponse.createResponse(PageCustomResponse.of(inquiryResponses));
    }

    /**
     * 내 문의하기 수
     *
     * @param memberPublicId
     * @return
     */
    public int getInquiriesCountByMember(UUID memberPublicId) {
        return inquiryQueryRepository.getMyInquires(memberPublicId);
    }

    /**
     * 문의 내역 상세 조회
     */
    @CountView(domain = DomainType.INQUIRY, idParam = "inquiryId")
    public InquiryDetailsResponse getInquiryById(final Long inquiryId) {
        Member member = securityReadService.getMember();
        log.info("요청 멤버: {}, 조회 문의내역: {} 요청", member.getPublicId(), inquiryId);

        Inquiry inquiry = findInquiryById(inquiryId);
        inquiry.verifyInquiryAuthor(member);

        CommonCountDto commonCount = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.INQUIRY, inquiryId,
                null);

        log.info("요청 멤버: {}, 조회 문의내역: {} 성공", member.getPublicId(), inquiryId);

        Long previousId = inquiryQueryRepository.findPreviousInquiry(inquiry.getId(), member.getPublicId());
        Long nextId = inquiryQueryRepository.findNextInquiryId(inquiry.getId(), member.getPublicId());

        return InquiryDetailsResponse.createResponse(inquiry, commonCount, previousId, nextId);
    }

    public Inquiry findInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.INQUIRY_NOT_FOUND));
        return inquiry;
    }

    public boolean existsById(Long id) {
        return inquiryRepository.existsById(id);
    }
}