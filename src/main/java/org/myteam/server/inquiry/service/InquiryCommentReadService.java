//package org.myteam.server.inquiry.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.myteam.server.global.exception.ErrorCode;
//import org.myteam.server.global.exception.PlayHiveException;
//import org.myteam.server.inquiry.domain.InquiryComment;
//import org.myteam.server.inquiry.dto.response.InquiryCommentResponse.*;
//import org.myteam.server.inquiry.repository.InquiryCommentQueryRepository;
//import org.myteam.server.inquiry.repository.InquiryCommentRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class InquiryCommentReadService {
//
//    private final InquiryCommentRepository inquiryCommentRepository;
//    private final InquiryCommentQueryRepository inquiryCommentQueryRepository;
//
//    public InquiryComment findById(Long inquiryCommentId) {
//        return inquiryCommentRepository.findById(inquiryCommentId)
//                .orElseThrow(() -> new PlayHiveException(ErrorCode.INQUIRY_COMMENT_NOT_FOUND));
//    }
//
//    public InquiryCommentListResponse findByInquiryId(Long inquiryId) {
//        log.info("문의내역: {}에 대한 댓글 목록 조회 요청", inquiryId);
//        List<InquiryComment> list = inquiryCommentRepository.findByInquiryId(inquiryId);
//        List<InquiryCommentSaveResponse> responseList = InquiryCommentSaveResponse.convertToResponseList(list);
//        log.info("문의내역: {}에 대한 댓글 목록 조회 성공", inquiryId);
//
//        return InquiryCommentListResponse.createResponse(responseList);
//    }
//
//    public InquiryCommentSaveResponse findByIdWithReply(Long inquiryCommentId) {
//        log.info("문의내역 댓글: {}에 대한 대댓글 목록 조회 요청", inquiryCommentId);
//        InquiryComment InquiryComment = findById(inquiryCommentId);
//        InquiryCommentSaveResponse response = InquiryCommentSaveResponse.createResponse(InquiryComment, InquiryComment.getMember());
//
//        response.setBoardReplyList(inquiryCommentQueryRepository.getRepliesForComments(inquiryCommentId));
//        log.info("문의내역 댓글: {}에 대한 대댓글 목록 조회 성공", inquiryCommentId);
//
//        return response;
//    }
//
//    public boolean existsById(Long id) {
//        return inquiryCommentRepository.existsById(id);
//    }
//
//    public int getCommentCountByMemberPublicId(UUID publicId) {
//        return inquiryCommentQueryRepository.getCommentCountByPublicId(publicId);
//    }
//
//    public List<InquiryCommentSaveResponse> findBestByInquiryId(Long inquiryId) {
//        return inquiryCommentQueryRepository.getInquiryBestCommentList(inquiryId);
//    }
//}
