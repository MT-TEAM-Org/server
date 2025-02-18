package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.domain.InquiryRecommend;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.myteam.server.inquiry.repository.InquiryRecommendRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryCountService {

    private final InquiryReadService inquiryReadService;
    private final SecurityReadService securityReadService;
    private final InquiryRecommendRepository inquiryRecommendRepository;
    private final InquiryCountReadService inquiryCountReadService;
    private final InquiryRecommendReadService inquiryRecommendReadService;
    private final InquiryCountRepository inquiryCountRepository;


    /**
     * TODO: 익명 파악해보기
     * @param inquiryId
     */
    public void recommendInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(inquiry, member);

        recommend(inquiry, member);
        addRecommendCount(inquiry.getId());
    }

    public void deleteRecommendInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(inquiry, member);

        inquiryRecommendRepository.deleteByInquiryIdAndMemberPublicId(inquiry.getId(), member.getPublicId());

        minusRecommendCount(inquiryId);
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(Inquiry inquiry, Member member) {
        inquiryRecommendReadService.confirmExistInquiryRecommend(inquiry.getId(), member.getPublicId());
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(Inquiry inquiry, Member member) {
        inquiryRecommendReadService.isAlreadyRecommended(inquiry.getId(), member.getPublicId());
    }

    /**
     * 게시글 추천 생성
     */
    private void recommend(Inquiry inquiry, Member member) {
        InquiryRecommend recommend = InquiryRecommend.builder()
                .inquiry(inquiry)
                .member(member)
                .build();
        inquiryRecommendRepository.save(recommend);
    }

    /**
     * recommendCount 증가
     */
    public void addRecommendCount(Long inquiryId) {
        InquiryCount inquiryCount = inquiryCountReadService.findByInquiryId(inquiryId);
        inquiryCount.addRecommendCount();
        inquiryCountRepository.save(inquiryCount);
    }

    /**
     * recommendCount 감소
     */
    public void minusRecommendCount(Long inquiryId) {
        InquiryCount inquiryCount = inquiryCountReadService.findByInquiryId(inquiryId);
        inquiryCount.minusRecommendCount();
        inquiryCountRepository.save(inquiryCount);
    }

    /**
     * commentCount 증가
     */
    public void addCommentCount(Long inquiryId) {
        InquiryCount inquiryCount = inquiryCountReadService.findByInquiryId(inquiryId);
        inquiryCount.addCommentCount();
        inquiryCountRepository.save(inquiryCount);
    }

    public void minusCommentCount(Long inquiryId) {
        inquiryCountReadService.findByInquiryId(inquiryId).minusCommentCount();
    }

    /**
     * commentCount 감소
     */
    public void minusCommentCount(Long inquiryId, int count) {
        InquiryCount inquiryCount = inquiryCountReadService.findByInquiryId(inquiryId);
        inquiryCount.minusCommentCount(count);
        inquiryCountRepository.save(inquiryCount);
    }
}
