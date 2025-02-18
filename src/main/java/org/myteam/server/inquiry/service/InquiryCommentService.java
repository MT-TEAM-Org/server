package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.inquiry.dto.request.InquiryCommentRequest;
import org.myteam.server.inquiry.dto.request.InquiryCommentUpdateRequest;
import org.myteam.server.inquiry.dto.response.InquiryCommentResponse;
import org.myteam.server.inquiry.repository.InquiryCommentRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
//import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
//    private final S3Service s3Service;
    private final InquiryReplyReadService inquiryReplyReadService;
    private final InquiryReplyService inquiryReplyService;

    /**
     * 문의 내역 댓글 생성
     * TODO: 익명 사용자 확인해보기
     * @param inquiryId
     * @param request
     * @param createdIp
     * @return
     */
    public InquiryCommentResponse save(Long inquiryId, InquiryCommentRequest request, String createdIp) {
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);
        Member member = securityReadService.getMember();

        InquiryComment inquiryComment = InquiryComment.createComment(inquiry, member, request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), createdIp);

        inquiryCommentRepository.save(inquiryComment);

        inquiryCountService.addCommentCount(inquiry.getId());

        return InquiryCommentResponse.createResponse(inquiryComment, member);
    }

    /**
     * 문의내역 댓글 수정
     * @param inquiryCommentId
     * @param request
     * @return
     */
    public InquiryCommentResponse update(Long inquiryCommentId, InquiryCommentUpdateRequest request) {
        Member member = securityReadService.getMember();
        InquiryComment inquiryComment = inquiryCommentReadService.findById(inquiryCommentId);

        inquiryComment.verifyInquiryCommentAuthor(member);
        verifyInquiryCommentImageAndRequestImage(inquiryComment.getImageUrl(), request.getImageUrl());

        inquiryComment.updateComment(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()));
        inquiryCommentRepository.save(inquiryComment);

        return InquiryCommentResponse.createResponse(inquiryComment, member);
    }

    /**
     * 문의내역 댓글 삭제
     * TODO: 익명 사용자 확인
     * @param inquiryCommentId
     */
    public void deleteInquiryComment(Long inquiryCommentId) {
        Member member = securityReadService.getMember();
        InquiryComment inquiryComment = inquiryCommentReadService.findById(inquiryCommentId);

        inquiryComment.verifyInquiryCommentAuthor(member);

//        s3Service.deleteFile(MediaUtils.getImagePath(inquiryComment.getImageUrl()));
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
//            s3Service.deleteFile(MediaUtils.getImagePath(inquiryReply.getImageUrl()));
            inquiryReplyService.deleteReply(inquiryReply);
        }
        return inquiryReplyList.size();
    }

    /**
     * 기존 이미지와 요청 이미지가 같지 않으면 삭제
     */
    private void verifyInquiryCommentImageAndRequestImage(String inquiryCommentImageUrl, String requestImageUrl) {
        if (!inquiryCommentImageUrl.equals(requestImageUrl)) {
//            s3Service.deleteFile(MediaUtils.getImagePath(requestImageUrl));
        }
    }
}
