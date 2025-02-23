package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.dto.ImprovementRequest.*;
import org.myteam.server.improvement.dto.ImprovementResponse.*;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.Repository.NoticeCountRepository;
import org.myteam.server.notice.Repository.NoticeRepository;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.dto.response.NoticeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImprovementService {
    private final ImprovementRepository improvementRepository;
    private final ImprovementCountRepository improvementCountRepository;
    private final SecurityReadService securityReadService;
    private final ImprovementRecommendReadService improvementRecommendReadService;
    private final ImprovementCountReadService improvementCountReadService;
    private final ImprovementReadService improvementReadService;

    /**
     * 개선요청 작성
     */
    public ImprovementSaveResponse saveImprovement(ImprovementSaveRequest request, String clientIp) {
        log.info("개선요청 시도");
        Member member = securityReadService.getMember();

        Improvement improvement = makeImprovement(member, clientIp, request);

        ImprovementCount improvementCount = ImprovementCount.createImprovementCount(improvement);
        improvementCountRepository.save(improvementCount);

        boolean isRecommended = improvementRecommendReadService.isRecommended(improvement.getId(), member.getPublicId());

        log.info("개선요청 생성: {}", improvement.getId());
        return ImprovementSaveResponse.createResponse(improvement, improvementCount, isRecommended);
    }

    /**
     * 개선요청 수정
     */
    public ImprovementSaveResponse updateImprovement(ImprovementSaveRequest request, Long improvementId) {
        log.info("update improvement 실행");
        Member member = securityReadService.getMember();

        Improvement improvement = improvementReadService.findById(improvementId);
        if (improvement.getMember().getPublicId() != member.getPublicId()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        improvement.updateImprovement(request.getTitle(), request.getContent(), request.getImgUrl());
        improvementRepository.save(improvement);

        ImprovementCount improvementCount = improvementCountReadService.findByImprovementId(improvementId);

        boolean isRecommended = improvementRecommendReadService.isRecommended(improvement.getId(), member.getPublicId());
        log.info("개선요청 수정: {}", improvement.getId());
        return ImprovementSaveResponse.createResponse(improvement, improvementCount, isRecommended);
    }

    /**
     * 개선요청 상태 업데이트
     */
    public void updateImprovementStatus(Long improvementId) {
        Member member = securityReadService.getMember();
        Improvement improvement = improvementReadService.findById(improvementId);

        if (!member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        improvement.updateState();
    }

    /**
     * 개선요청 삭제
     */
    public void deleteImprovement(Long improvementId) {
        log.info("delete improvement 실행");
        Member member = securityReadService.getMember();

        Improvement improvement = improvementReadService.findById(improvementId);
        if (improvement.getMember().getPublicId() != member.getPublicId()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        improvementCountRepository.deleteByImprovementId(improvement.getId());
        improvementRepository.delete(improvement);

        log.info("개선요청 삭제: {}", improvementId);
    }

    private Improvement makeImprovement(Member member, String clientIp, ImprovementSaveRequest request) {
        Improvement improvement = Improvement.builder()
                .member(member)
                .createdIP(clientIp)
                .title(request.getTitle())
                .content(request.getContent())
                .imgUrl(request.getImgUrl())
                .build();

        improvementRepository.save(improvement);
        return improvement;
    }
}
