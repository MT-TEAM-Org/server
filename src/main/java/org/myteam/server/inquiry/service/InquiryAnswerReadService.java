package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryAnswer;
import org.myteam.server.inquiry.repository.InquiryAnswerRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryAnswerReadService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;

    public void getAnswer(Long inquiryId) {
        if (inquiryRepository.existsById(inquiryId)) {
            throw new PlayHiveException(ErrorCode.INQUIRY_NOT_FOUND);
        }

        inquiryAnswerRepository.findByInquiryId(inquiryId);

        log.info("성공적으로 문의내역Id: {} 조회되었습니다.", inquiryId);
    }
}
