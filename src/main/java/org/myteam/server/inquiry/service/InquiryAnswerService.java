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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryAnswerService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;

    public void createAnswer(Long inquiryId, String content) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.INQUIRY_NOT_FOUND));

        Optional<InquiryAnswer> existingAnswer = Optional.ofNullable(inquiryAnswerRepository.findByInquiryId(inquiry.getId()));

        if (existingAnswer.isPresent()) {
            throw new PlayHiveException(ErrorCode.INQUIRY_ANSWER_ALREADY_EXISTS);
        }

        InquiryAnswer answer = InquiryAnswer.createAnswer(inquiry, content);
        inquiryAnswerRepository.save(answer);

        inquiry.addAnswer(answer);
        inquiryRepository.save(inquiry);


        log.info("성공적으로 문의내역 ID: {}에 대해 답변이 달렸습니다", inquiryId);
    }

    public void updateAnswer(Long inquiryId, String newContent) {
        if (!inquiryAnswerRepository.existsById(inquiryId)) {
            throw new PlayHiveException(ErrorCode.INQUIRY_ANSWER_NOT_FOUND);
        }
        InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiryId);

        inquiryAnswer.updateContent(newContent);
        log.info("문의내역 ID: {}의 답변이 수정되었습니다.", inquiryId);
    }

    public void deleteAnswer(Long inquiryId) {
        if (!inquiryAnswerRepository.existsById(inquiryId)) {
            throw new PlayHiveException(ErrorCode.INQUIRY_ANSWER_NOT_FOUND);
        }
        InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiryId);
        inquiryAnswerRepository.delete(inquiryAnswer);

        log.info("문의내역 ID: {}의 답변이 삭제되었습니다.", inquiryId);
    }
}
