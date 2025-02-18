package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.inquiry.repository.InquiryReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryReplyService {

    private final InquiryReplyRepository inquiryReplyRepository;

    public void deleteReply(InquiryReply inquiryReply) {
        inquiryReplyRepository.delete(inquiryReply);
    }
}
