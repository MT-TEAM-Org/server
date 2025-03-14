package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.service.CommentCountService;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.domain.InquiryRecommend;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryCountService implements CommentCountService {
    private final InquiryCountReadService inquiryCountReadService;
    private final InquiryCountRepository inquiryCountRepository;

    /**
     * commentCount 증가
     */
    @Override
    public void addCommentCount(Long inquiryId) {
        InquiryCount inquiryCount = inquiryCountReadService.findByInquiryId(inquiryId);
        inquiryCount.addCommentCount();
        inquiryCountRepository.save(inquiryCount);
    }

    @Override
    public void minusCommentCount(Long inquiryId) {
        inquiryCountReadService.findByInquiryId(inquiryId).minusCommentCount();
    }

    /**
     * commentCount 감소
     */
    @Override
    public void minusCommentCount(Long inquiryId, int count) {
        InquiryCount inquiryCount = inquiryCountReadService.findByInquiryId(inquiryId);
        inquiryCount.minusCommentCount(count);
        inquiryCountRepository.save(inquiryCount);
    }
}
