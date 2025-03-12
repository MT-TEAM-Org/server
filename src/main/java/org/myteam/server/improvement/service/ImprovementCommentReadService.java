package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.improvement.domain.ImprovementComment;
import org.myteam.server.improvement.domain.ImprovementOrderType;
import org.myteam.server.improvement.dto.response.ImprovementCommentResponse.*;
import org.myteam.server.improvement.repository.ImprovementCommentQueryRepository;
import org.myteam.server.improvement.repository.ImprovementCommentRepository;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImprovementCommentReadService {

    private final ImprovementCommentRepository improvementCommentRepository;
    private final ImprovementCommentQueryRepository improvementCommentQueryRepository;
    private final ImprovementCommentRecommendReadService improvementCommentRecommendReadService;
    private final SecurityReadService securityReadService;

    public ImprovementComment findById(Long improvementCommentId) {
        return improvementCommentRepository.findById(improvementCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_COMMENT_NOT_FOUND));
    }

    public ImprovementCommentListResponse findByImprovementId(Long improvementId, ImprovementOrderType orderType) {
        log.info("개선요청 {} 댓글 목록 조회 시도", improvementId);

        UUID loginUser = securityReadService.getAuthenticatedPublicId();

        List<ImprovementCommentSaveResponse> list = improvementCommentQueryRepository.getImprovementCommentList(
                improvementId,
                orderType,
                loginUser
        );

        log.info("개선요청 {} 댓글 목록 조회 성공", improvementId);
        return ImprovementCommentListResponse.createResponse(list);
    }

    public ImprovementCommentSaveResponse findByIdWithReply(Long improvementCommentId) {
        log.info("개선요청 댓글: {} 상세 조회 시도", improvementCommentId);
        ImprovementComment improvementComment = findById(improvementCommentId);

        boolean issRecommended = false;

        UUID loginUser = securityReadService.getAuthenticatedPublicId();

        if (loginUser != null) {
            issRecommended = improvementCommentRecommendReadService.isRecommended(improvementComment.getId(), loginUser);
        }

        ImprovementCommentSaveResponse response = ImprovementCommentSaveResponse.createResponse(improvementComment, improvementComment.getMember(), issRecommended);
        response.setImprovementReplyList(improvementCommentQueryRepository.getRepliesForComments(improvementComment.getId(), loginUser));

        log.info("개선요청 댓글: {} 상세 조회 성공", improvementCommentId);
        return response;
    }

    public int getCommentCountByMemberPublicId(UUID publicId) {
        return improvementCommentQueryRepository.getCommentCountByPublicId(publicId);
    }

    public List<ImprovementCommentSaveResponse> findBestByImprovementId(Long improvementId) {
        return improvementCommentQueryRepository.getImprovementBestCommentList(improvementId);
    }
}
