package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.inquiry.dto.request.InquiryCommentRequest.*;
import org.myteam.server.inquiry.dto.response.InquiryCommentResponse.*;
import org.myteam.server.inquiry.repository.InquiryCommentRepository;
import org.myteam.server.inquiry.repository.InquiryReplyRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryCommentService {

    private final InquiryReadService inquiryReadService;
    private final SecurityReadService securityReadService;
    private final BadWordFilter badWordFilter;
    private final InquiryCommentRepository inquiryCommentRepository;
    private final InquiryCountService inquiryCountService;
    private final InquiryCommentReadService inquiryCommentReadService;
    private final S3Service s3Service;
    private final InquiryReplyReadService inquiryReplyReadService;
    private final InquiryReplyRepository inquiryReplyRepository;
    private final InquiryRepository inquiryRepository;

    /**
     * 문의 내역 댓글 생성
     * @param inquiryId
     * @param request
     * @param createdIp
     * @return
     */
    public InquiryCommentSaveResponse save(Long inquiryId, InquiryCommentSaveRequest request, String createdIp) {
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);
        Member member = securityReadService.getMember();

        InquiryComment inquiryComment = InquiryComment.createComment(inquiry, member, request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), createdIp);

        inquiryCommentRepository.save(inquiryComment);

        inquiryCountService.addCommentCount(inquiry.getId());

        if (member.isAdmin()) {
            inquiry.updateAdminAnswered();
            inquiryRepository.save(inquiry);
        }

        return InquiryCommentSaveResponse.createResponse(inquiryComment, member);
    }

    /**
     * 문의내역 댓글 수정
     * @param inquiryCommentId
     * @param request
     * @return
     */
    public InquiryCommentSaveResponse update(Long inquiryCommentId, InquiryCommentUpdateRequest request) {
        Member member = securityReadService.getMember();
        InquiryComment inquiryComment = inquiryCommentReadService.findById(inquiryCommentId);

        inquiryComment.verifyInquiryCommentAuthor(member);
        if (MediaUtils.verifyImageUrlAndRequestImageUrl(inquiryComment.getImageUrl(), request.getImageUrl())) {
            s3Service.deleteFile(MediaUtils.getImagePath(inquiryComment.getImageUrl()));
        }

        inquiryComment.updateComment(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()));
        inquiryCommentRepository.save(inquiryComment);

        return InquiryCommentSaveResponse.createResponse(inquiryComment, member);
    }

    /**
     * 문의내역 댓글 삭제
     * @param inquiryCommentId
     */
    public void deleteInquiryComment(Long inquiryCommentId) {
        Member member = securityReadService.getMember();
        InquiryComment inquiryComment = inquiryCommentReadService.findById(inquiryCommentId);

        inquiryComment.verifyInquiryCommentAuthor(member);

        s3Service.deleteFile(MediaUtils.getImagePath(inquiryComment.getImageUrl()));
        int minusCount = deleteBoardReply(inquiryComment.getId());
        inquiryCommentRepository.deleteById(inquiryCommentId);

        inquiryCountService.minusCommentCount(inquiryComment.getInquiry().getId(), minusCount + 1);
    }

    /**
     * 대댓글 삭제
     */
    private int deleteBoardReply(Long boardCommentId) {
        List<InquiryReply> inquiryReplyList = inquiryReplyReadService.findByBoardCommentId(boardCommentId);
        for (InquiryReply inquiryReply : inquiryReplyList) {
            s3Service.deleteFile(MediaUtils.getImagePath(inquiryReply.getImageUrl()));
            inquiryReplyRepository.delete(inquiryReply);
        }
        return inquiryReplyList.size();
    }
}
