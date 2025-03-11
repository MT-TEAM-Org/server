package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.inquiry.dto.request.InquiryCommentRequest.*;
import org.myteam.server.inquiry.dto.response.InquiryCommentResponse.*;
import org.myteam.server.inquiry.repository.InquiryReplyRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryReplyService {

    private final InquiryReplyRepository inquiryReplyRepository;
    private final SecurityReadService securityReadService;
    private final InquiryCommentReadService inquiryCommentReadService;
    private final MemberReadService memberReadService;
    private final BadWordFilter badWordFilter;
    private final InquiryCountService inquiryCountService;
    private final InquiryReplyReadService inquiryReplyReadService;
    private final S3Service s3Service;

    /**
     * 문의내역 대댓글 생성
     * @param inquiryCommentId
     * @param request
     * @param createdIp
     * @return
     */
    public InquiryReplyResponse save(Long inquiryCommentId, InquiryReplySaveRequest request, String createdIp) {
        log.info("댓글: {}에 대한 대댓글 생성 요청", inquiryCommentId);

        Member member = securityReadService.getMember();
        InquiryComment inquiryComment = inquiryCommentReadService.findById(inquiryCommentId);

        Member mentionMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        InquiryReply inquiryReply = InquiryReply.createInquiryReply(
                inquiryComment, member, request.getImageUrl(), badWordFilter.filterMessage(request.getComment()), createdIp, mentionMember
        );
        inquiryReplyRepository.save(inquiryReply);
        log.info("댓글: {}에 대한 대댓글: {} 생성", inquiryReply.getId());

        inquiryCountService.addCommentCount(inquiryComment.getInquiry().getId());

        return InquiryReplyResponse.createResponse(inquiryReply, member, mentionMember);
    }

    /**
     * 문의내역 대댓글 수정
     * @param inquiryReplyId
     * @param request
     * @return
     */
    public InquiryReplyResponse update(Long inquiryReplyId, InquiryReplySaveRequest request) {
        log.info("대댓글: {}에 대한 수정 요청", inquiryReplyId);
        Member member = securityReadService.getMember();
        InquiryReply inquiryReply = inquiryReplyReadService.findById(inquiryReplyId);

        inquiryReply.verifyBoardReplyAuthor(member);
        if (MediaUtils.verifyImageUrlAndRequestImageUrl(inquiryReply.getImageUrl(), request.getImageUrl())) {
            s3Service.deleteFile(inquiryReply.getImageUrl());
        }

        Member mentionedMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        inquiryReply.updateReply(request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), mentionedMember);
        inquiryReplyRepository.save(inquiryReply);
        log.info("대댓글: {}에 대한 수정", inquiryReplyId);

        return InquiryReplyResponse.createResponse(inquiryReply, member, mentionedMember);
    }

    public void deleteReply(Long inquiryReplyId) {
        log.info("대댓글: {} 삭제 요청", inquiryReplyId);
        Member member = securityReadService.getMember();
        InquiryReply inquiryReply = inquiryReplyReadService.findById(inquiryReplyId);

        inquiryReply.verifyBoardReplyAuthor(member);

        s3Service.deleteFile(MediaUtils.getImagePath(inquiryReply.getImageUrl()));
        inquiryReplyRepository.delete(inquiryReply);
        log.info("대댓글: {} 삭제 ", inquiryReplyId);

        inquiryCountService.minusCommentCount(inquiryReply.getInquiryComment().getInquiry().getId());
    }
}
