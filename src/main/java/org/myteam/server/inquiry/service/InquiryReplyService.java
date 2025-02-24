package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.inquiry.dto.request.InquiryReplySaveRequest;
import org.myteam.server.inquiry.dto.response.InquiryReplyResponse;
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
        Member member = securityReadService.getMember();
        InquiryComment inquiryComment = inquiryCommentReadService.findById(inquiryCommentId);

        Member mentionMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        InquiryReply inquiryReply = InquiryReply.createInquiryReply(
                inquiryComment, member, request.getImageUrl(), badWordFilter.filterMessage(request.getComment()), createdIp, mentionMember
        );
        inquiryReplyRepository.save(inquiryReply);

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
        Member member = securityReadService.getMember();
        InquiryReply inquiryReply = inquiryReplyReadService.findById(inquiryReplyId);

        inquiryReply.verifyBoardReplyAuthor(member);
        verifyInquiryReplyImageAndRequestImage(inquiryReply.getImageUrl(), request.getImageUrl());

        Member mentionedMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        inquiryReply.updateReply(
                request.getImageUrl(), badWordFilter.filterMessage(request.getComment()), mentionedMember
        );
        inquiryReplyRepository.save(inquiryReply);

        return InquiryReplyResponse.createResponse(inquiryReply, member, mentionedMember);
    }

    public void deleteReply(Long inquiryReplyId) {
        Member member = securityReadService.getMember();
        InquiryReply inquiryReply = inquiryReplyReadService.findById(inquiryReplyId);

        inquiryReply.verifyBoardReplyAuthor(member);

        s3Service.deleteFile(MediaUtils.getImagePath(inquiryReply.getImageUrl()));
        inquiryReplyRepository.delete(inquiryReply);

        inquiryCountService.minusCommentCount(inquiryReply.getInquiryComment().getInquiry().getId());
    }

    private void verifyInquiryReplyImageAndRequestImage(String inquiryReplyImageUrl, String requestImageUrl) {
        if (!inquiryReplyImageUrl.equals(requestImageUrl)) {
            s3Service.deleteFile(MediaUtils.getImagePath(requestImageUrl));
        }
    }
}
