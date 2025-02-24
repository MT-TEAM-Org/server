package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementComment;
import org.myteam.server.improvement.domain.ImprovementReply;
import org.myteam.server.improvement.dto.request.ImprovementCommentRequest.*;
import org.myteam.server.improvement.dto.response.ImprovementCommentResponse.*;
import org.myteam.server.improvement.repository.ImprovementCommentRepository;
import org.myteam.server.improvement.repository.ImprovementReplyRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeComment;
import org.myteam.server.notice.domain.NoticeReply;
import org.myteam.server.notice.dto.request.NoticeCommentRequest;
import org.myteam.server.notice.dto.response.NoticeCommentResponse;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImprovementCommentService {

    private final ImprovementReadService improvementReadService;
    private final SecurityReadService securityReadService;
    private final BadWordFilter badWordFilter;
    private final ImprovementCommentRepository improvementCommentRepository;
    private final ImprovementCountService improvementCountService;
    private final ImprovementRecommendReadService improvementRecommendReadService;
    private final ImprovementCommentReadService improvementCommentReadService;
    private final S3Service s3Service;
    private final ImprovementCommentRecommendReadService improvementCommentRecommendReadService;
    private final ImprovementReplyRepository improvementReplyRepository;
    private final ImprovementReplyReadService improvementReplyReadService;

    /**
     * 개선요청 댓글 생성
     */
    public ImprovementCommentSaveResponse save(Long improvementId, ImprovementCommentSaveRequest request, String createdIp) {
        log.info("개선요청: {}의 댓글 생성 시도", improvementId);
        Improvement improvement = improvementReadService.findById(improvementId);
        Member member = securityReadService.getMember();

        ImprovementComment improvementComment = ImprovementComment.createImprovementComment(improvement, member,
                request.getImageUrl(), badWordFilter.filterMessage(request.getComment()), createdIp);

        improvementCommentRepository.save(improvementComment);
        improvementCountService.addCommentCount(improvement.getId());

        boolean isRecommended = improvementRecommendReadService.isRecommended(improvementComment.getId(), member.getPublicId());

        log.info("개선요청: {}의 댓글 생성 성공", improvementId);

        return ImprovementCommentSaveResponse.createResponse(improvementComment, member, isRecommended);
    }

    /**
     * 개선요청 댓글 수정
     */
    public ImprovementCommentSaveResponse update(Long improvementCommentId, ImprovementCommentUpdateRequest request) {
        log.info("개선요청: {}의 댓글 수정 시도", improvementCommentId);
        Member member = securityReadService.getMember();
        ImprovementComment improvementComment = improvementCommentReadService.findById(improvementCommentId);

        improvementComment.verifyNoticeCommentAuthor(member);
        if (!MediaUtils.verifyImageUrlAndRequestImageUrl(improvementComment.getImageUrl(), request.getImageUrl())) {
            s3Service.deleteFile(MediaUtils.getImagePath(request.getImageUrl()));
        }

        improvementComment.updateComment(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()));

        boolean isRecommended = improvementCommentRecommendReadService.isRecommended(improvementComment.getId(), member.getPublicId());

        log.info("개선요청: {}의 댓글 수정 성공", improvementCommentId);

        return ImprovementCommentSaveResponse.createResponse(improvementComment, member, isRecommended);
    }

    /**
     * 개선요청 댓글 삭제
     */
    public void deleteImprovementComment(Long improvementCommentId) {
        log.info("개선요청: {}의 댓글 삭제 시도", improvementCommentId);
        Member member = securityReadService.getMember();
        ImprovementComment improvementComment = improvementCommentReadService.findById(improvementCommentId);

        improvementComment.verifyNoticeCommentAuthor(member);

        // S3 이미지 삭제
        if (improvementComment.getImageUrl() != null) {
            s3Service.deleteFile(MediaUtils.getImagePath(improvementComment.getImageUrl()));
        }

        // 대댓글 삭제 (카운트도 포함)
        int minusCount = deleteImprovementReply(improvementComment.getId());
        // 댓글 삭제
        improvementCommentRepository.deleteById(improvementCommentId);

        // 댓글 카운트 감소
        improvementCountService.minusCommentCount(improvementComment.getImprovement().getId(), minusCount + 1);

        log.info("개선요청 댓글: {} 삭제 성공", improvementCommentId);
    }

    /**
     * 대댓글 삭제
     */
    private int deleteImprovementReply(Long improvementCommentId) {
        List<ImprovementReply> improvementReplyList = improvementReplyReadService.findByImprovementCommentId(improvementCommentId);
        for (ImprovementReply improvementReply : improvementReplyList) {
            s3Service.deleteFile(MediaUtils.getImagePath(improvementReply.getImageUrl()));
            improvementReplyRepository.delete(improvementReply);
        }
        return improvementReplyList.size();
    }
}
