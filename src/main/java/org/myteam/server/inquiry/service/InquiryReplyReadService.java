package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.inquiry.repository.InquiryReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryReplyReadService {

    private final InquiryReplyRepository inquiryReplyRepository;

    public InquiryReply findById(Long inquiryReplyId) {
        return inquiryReplyRepository.findById(inquiryReplyId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.INQUIRY_REPLY_NOT_FOUND));
    }

    public List<InquiryReply> findByBoardCommentId(Long inquiryCommentId) {
        return inquiryReplyRepository.findByInquiryCommentId(inquiryCommentId);
    }
}
