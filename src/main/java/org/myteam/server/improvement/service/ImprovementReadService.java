package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.dto.ImprovementRequest.*;
import org.myteam.server.improvement.dto.ImprovementResponse.*;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.myteam.server.improvement.repository.ImprovementQueryRepository;
import org.myteam.server.improvement.repository.ImprovementRecommendRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImprovementReadService {

    private final ImprovementRepository improvementRepository;
    private final ImprovementCountReadService improvementCountReadService;
    private final MemberRepository memberRepository;
    private final ImprovementRecommendReadService improvementRecommendReadService;
    private final ImprovementQueryRepository improvementQueryRepository;

    public Improvement findById(Long improvementId) {
        return improvementRepository.findById(improvementId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_NOT_FOUND));
    }

    /**
     * 개선요청 상세 조회
     */
    public ImprovementSaveResponse getImprovement(Long improvementId, CustomUserDetails userDetails) {
        log.info("개선요청: {} 상세 조회 호출", improvementId);

        Improvement improvement = findById(improvementId);
        ImprovementCount improvementCount = improvementCountReadService.findByImprovementId(improvementId);

        boolean isRecommended = false;

        if (userDetails != null) {
            UUID memberPublicId = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
            isRecommended = improvementRecommendReadService.isRecommended(improvement.getId(), memberPublicId);
        }

        log.info("개선요청 상세 조회 성공: {}", improvementId);
        return ImprovementSaveResponse.createResponse(improvement, improvementCount, isRecommended);
    }

    /**
     * 개선요청 목록 조회
     */
    public ImprovementListResponse getImprovementList(ImprovementServiceRequest request) {
        log.info("개선요청 목록 조회 호출");
        Page<ImprovementDto> improvementPagingList = improvementQueryRepository.getImprovementList(
                request.getOrderType(),
                request.getSearchType(),
                request.getSearch(),
                request.toPageable()
        );

        log.info("개선요청 목록 조회 호출");

        return ImprovementListResponse.createResponse(PageCustomResponse.of(improvementPagingList));
    }
}
