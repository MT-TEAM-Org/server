package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.improvement.domain.ImprovementComment;
import org.myteam.server.improvement.domain.ImprovementReply;
import org.myteam.server.improvement.dto.request.ImprovementCommentRequest.*;
import org.myteam.server.improvement.dto.response.ImprovementCommentResponse.*;
import org.myteam.server.improvement.repository.ImprovementReplyRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.domain.NoticeReply;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImprovementReplyService {

    private final SecurityReadService securityReadService;
    private final ImprovementCommentReadService improvementCommentReadService;
    private final MemberReadService memberReadService;
    private final BadWordFilter badWordFilter;
    private final ImprovementCountService improvementCountService;
    private final ImprovementReplyRecommendReadService improvementReplyRecommendReadService;
    private final ImprovementReplyReadService improvementReplyReadService;
    private final S3Service s3Service;
    private final ImprovementReplyRepository improvementReplyRepository;

    /**
     * 개선요청 대댓글 생성
     */
    public ImprovementReplyResponse saveReply(Long improvementCommentId, ImprovementReplySaveRequest request, String createdIp) {
        log.info("개선요청 대댓글: {} 생성 시도", improvementCommentId);
        Member member = securityReadService.getMember();
        ImprovementComment improvementComment = improvementCommentReadService.findById(improvementCommentId);

        Member mentionedMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        ImprovementReply improvementReply = ImprovementReply.createImprovementReply(improvementComment, member, request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), createdIp, mentionedMember);

        improvementReplyRepository.save(improvementReply);

        log.info("개선요청 대댓글: {} 생성 성공", improvementCommentId);

        improvementCountService.addCommentCount(improvementComment.getImprovement().getId());
        boolean isRecommended = improvementReplyRecommendReadService.isRecommended(improvementReply.getId(), member.getPublicId());

        return ImprovementReplyResponse.createResponse(improvementReply, member, mentionedMember, isRecommended);
    }

    /**
     * 개선요청 대댓글 수정
     */
    public ImprovementReplyResponse update(Long improvementReplyId, ImprovementReplySaveRequest request) {
        log.info("개선요청 대댓글: {} 수정 시도", improvementReplyId);
        Member member = securityReadService.getMember();
        ImprovementReply improvementReply = improvementReplyReadService.findById(improvementReplyId);

        improvementReply.verifyNoticeReplyAuthor(member);
        if (MediaUtils.verifyImageUrlAndRequestImageUrl(improvementReply.getImageUrl(), request.getImageUrl())) {
            // 기존 이미지와 요청 이미지가 같지 않으면 삭제
            s3Service.deleteFile(MediaUtils.getImagePath(improvementReply.getImageUrl()));
        }

        Member mentionedMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        improvementReply.updateReply(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()), mentionedMember);
        improvementReplyRepository.save(improvementReply);

        log.info("개선요청 대댓글: {} 수정 성공", improvementReplyId);

        boolean isRecommended = improvementReplyRecommendReadService.isRecommended(improvementReply.getId(), member.getPublicId());

        return ImprovementReplyResponse.createResponse(improvementReply, member, mentionedMember, isRecommended);
    }

    /**
     * 공지사항 대댓글 삭제
     */
    public void delete(Long improvementReplyId) {
        Member member = securityReadService.getMember();
        ImprovementReply improvementReply = improvementReplyReadService.findById(improvementReplyId);

        improvementReply.verifyNoticeReplyAuthor(member);

        s3Service.deleteFile(MediaUtils.getImagePath(improvementReply.getImageUrl()));
        improvementReplyRepository.delete(improvementReply);

        improvementCountService.minusCommentCount(improvementReply.getImprovementComment().getImprovement().getId());
    }
}
