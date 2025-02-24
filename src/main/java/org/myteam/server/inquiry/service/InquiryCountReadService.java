package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryCountReadService {

    private final InquiryCountRepository inquiryCountRepository;

    public InquiryCount findByInquiryId(Long inquiryId) {
        return inquiryCountRepository.findByInquiryId(inquiryId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.INQUIRY_RECOMMEND_NOT_FOUND));
    }
}
