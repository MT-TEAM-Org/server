package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.inquiry.dto.response.InquiryCommentListResponse;
import org.myteam.server.inquiry.dto.response.InquiryCommentResponse;
import org.myteam.server.inquiry.repository.InquiryCommentQueryRepository;
import org.myteam.server.inquiry.repository.InquiryCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryCommentReadService {

    private final InquiryCommentRepository inquiryCommentRepository;
    private final InquiryCommentQueryRepository inquiryCommentQueryRepository;

    public InquiryComment findById(Long inquiryCommentId) {
        return inquiryCommentRepository.findById(inquiryCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_COMMENT_NOT_FOUND));
    }

    public InquiryCommentListResponse findByInquiryId(Long inquiryId) {
        List<InquiryComment> list = inquiryCommentRepository.findByInquiryId(inquiryId);
        List<InquiryCommentResponse> responseList = InquiryCommentResponse.convertToResponseList(list);

        return InquiryCommentListResponse.createResponse(responseList);
    }

    public InquiryCommentResponse findByIdWithReply(Long inquiryCommentId) {
        InquiryComment InquiryComment = findById(inquiryCommentId);
        InquiryCommentResponse response = InquiryCommentResponse.createResponse(InquiryComment, InquiryComment.getMember());

        response.setBoardReplyList(inquiryCommentQueryRepository.getRepliesForComments(inquiryCommentId));

        return response;
    }
}
