package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.inquiry.repository.InquiryCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryCommentReadService {

    private final InquiryCommentRepository inquiryCommentRepository;

    public InquiryComment findById(Long inquiryId) {
        return inquiryCommentRepository.findById(inquiryId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_COMMENT_NOT_FOUND));
    }
}
