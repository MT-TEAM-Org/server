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
    private final MemberRepository memberRepository;
    private final ImprovementCommentQueryRepository improvementCommentQueryRepository;
    private final ImprovementCommentRecommendReadService improvementCommentRecommendReadService;

    public ImprovementComment findById(Long improvementCommentId) {
        return improvementCommentRepository.findById(improvementCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_COMMENT_NOT_FOUND));
    }

    public ImprovementCommentListResponse findByImprovementId(Long improvementId, ImprovementOrderType orderType, CustomUserDetails userDetails) {
        log.info("개선요청 {} 댓글 목록 조회 시도", improvementId);
        List<ImprovementCommentSaveResponse> list = improvementCommentQueryRepository.getImprovementCommentList(
                improvementId,
                orderType,
                userDetails
        );
        log.info("개선요청 {} 댓글 목록 조회 성공", improvementId);
        return ImprovementCommentListResponse.createResponse(list);
    }

    public ImprovementCommentSaveResponse findByIdWithReply(Long improvementCommentId, CustomUserDetails userDetails) {
        log.info("개선요청 댓글: {} 상세 조회 시도", improvementCommentId);
        ImprovementComment improvementComment = findById(improvementCommentId);

        boolean issRecommended = false;

        if (userDetails != null) {
            UUID loginUser = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
            issRecommended = improvementCommentRecommendReadService.isRecommended(improvementComment.getId(), loginUser);
        }

        ImprovementCommentSaveResponse response = ImprovementCommentSaveResponse.createResponse(improvementComment, improvementComment.getMember(), issRecommended);
        response.setImprovementReplyList(improvementCommentQueryRepository.getRepliesForComments(improvementComment.getId(), userDetails));

        log.info("개선요청 댓글: {} 상세 조회 성공", improvementCommentId);
        return response;
    }
}
