package org.myteam.server.improvement.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.dto.request.ImprovementRequest.ImprovementServiceRequest;
import org.myteam.server.improvement.dto.response.ImprovementResponse.ImprovementDto;
import org.myteam.server.improvement.dto.response.ImprovementResponse.ImprovementListResponse;
import org.myteam.server.improvement.dto.response.ImprovementResponse.ImprovementSaveResponse;
import org.myteam.server.improvement.repository.ImprovementQueryRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.DomainType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SecurityReadService securityReadService;
    private final RedisCountService redisCountService;

    public Improvement findById(Long improvementId) {
        return improvementRepository.findById(improvementId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_NOT_FOUND));
    }

    /**
     * 개선요청 상세 조회
     */
    public ImprovementSaveResponse getImprovement(Long improvementId) {
        log.info("개선요청: {} 상세 조회 호출", improvementId);

        Improvement improvement = findById(improvementId);
        ImprovementCount improvementCount = improvementCountReadService.findByImprovementId(improvementId);
        int viewCount = redisCountService.getViewCountAndIncr(DomainType.IMPROVEMENT, improvementId);

        boolean isRecommended = false;

        UUID loginUserUUID = securityReadService.getAuthenticatedPublicId();

        if (loginUserUUID != null) {
            isRecommended = improvementRecommendReadService.isRecommended(improvement.getId(), loginUserUUID);
        }

        log.info("개선요청 상세 조회 성공: {}", improvementId);
        Long previousId = improvementQueryRepository.findPreviousImprovementId(improvement.getId());
        Long nextId = improvementQueryRepository.findNextImprovementId(improvement.getId());

        return ImprovementSaveResponse.createResponse(improvement, improvementCount, isRecommended, previousId, nextId,
                viewCount);
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

    public boolean existsById(Long commentId) {
        return improvementRepository.existsById(commentId);
    }
}
