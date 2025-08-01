package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.media.MediaUtils;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.improvement.dto.request.ImprovementRequest.ImprovementSaveRequest;
import org.myteam.server.improvement.dto.response.ImprovementResponse.ImprovementSaveResponse;
import org.myteam.server.improvement.repository.ImprovementCountRepository;
import org.myteam.server.improvement.repository.ImprovementQueryRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.upload.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImprovementService {
    private final ImprovementRepository improvementRepository;
    private final ImprovementQueryRepository improvementQueryRepository;
    private final ImprovementCountRepository improvementCountRepository;
    private final SecurityReadService securityReadService;
    private final ImprovementRecommendReadService improvementRecommendReadService;
    private final ImprovementReadService improvementReadService;
    private final CommentService commentService;
    private final StorageService s3Service;
    private final RedisCountService redisCountService;

    /**
     * 개선요청 작성
     */
    public ImprovementSaveResponse saveImprovement(ImprovementSaveRequest request, String clientIp) {
        log.info("개선요청 시도");
        Member member = securityReadService.getMember();

        Improvement improvement = makeImprovement(member, clientIp, request);

        ImprovementCount improvementCount = ImprovementCount.createImprovementCount(improvement);
        improvementCountRepository.save(improvementCount);

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT,
                improvement.getId(), null);

        log.info("개선요청 생성: {}", improvement.getId());

        Long previousId = improvementQueryRepository.findPreviousImprovementId(improvement.getId());
        Long nextId = improvementQueryRepository.findNextImprovementId(improvement.getId());

        return ImprovementSaveResponse.createResponse(improvement, false,
                previousId, nextId, commonCountDto);
    }

    /**
     * 개선요청 수정
     */
    public ImprovementSaveResponse updateImprovement(ImprovementSaveRequest request, Long improvementId) {
        log.info("update improvement 실행");
        Member member = securityReadService.getMember();

        Improvement improvement = improvementReadService.findById(improvementId);
        if (!improvement.getMember().getPublicId().equals(member.getPublicId())) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        if (MediaUtils.verifyImageUrlAndRequestImageUrl(improvement.getImgUrl(), request.getImgUrl())) {
            s3Service.deleteFile(improvement.getImgUrl());
        }

        improvement.updateImprovement(request.getTitle(), request.getContent(), request.getImgUrl(), request.getLink());
        improvementRepository.save(improvement);

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT,
                improvementId, null);

        boolean isRecommended = improvementRecommendReadService.isRecommended(improvement.getId(),
                member.getPublicId());
        log.info("개선요청 수정: {}", improvement.getId());

        Long previousId = improvementQueryRepository.findPreviousImprovementId(improvement.getId());
        Long nextId = improvementQueryRepository.findNextImprovementId(improvement.getId());

        return ImprovementSaveResponse.createResponse(improvement, isRecommended, previousId, nextId,
                commonCountDto);
    }

    /**
     * 개선요청 상태 업데이트
     */
    public ImprovementStatus updateImprovementStatus(Long improvementId, ImprovementStatus status) {
        Member member = securityReadService.getMember();
        Improvement improvement = improvementReadService.findById(improvementId);

        if (!member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        improvement.updateState(status);

        return improvement.getImprovementStatus();
    }

    /**
     * 개선요청 삭제
     */
    public void deleteImprovement(Long improvementId) {
        log.info("delete improvement 실행");
        Member member = securityReadService.getMember();

        Improvement improvement = improvementReadService.findById(improvementId);
        if (!improvement.getMember().getPublicId().equals(member.getPublicId())) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        if (improvement.getImgUrl() != null) {
            s3Service.deleteFile(improvement.getImgUrl());
        }

        redisCountService.removeCount(DomainType.IMPROVEMENT, improvementId);

        improvementCountRepository.deleteByImprovementId(improvement.getId());
        improvementRepository.delete(improvement);

        log.info("개선요청 삭제: {}", improvementId);

        commentService.deleteCommentByPost(CommentType.IMPROVEMENT, improvementId);
    }

    private Improvement makeImprovement(Member member, String clientIp, ImprovementSaveRequest request) {
        Improvement improvement = Improvement.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .createdIp(clientIp)
                .imgUrl(request.getImgUrl())
                .link(request.getLink())
                .build();

        improvementRepository.save(improvement);
        return improvement;
    }
}
