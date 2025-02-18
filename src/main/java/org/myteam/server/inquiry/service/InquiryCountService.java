package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryRecommend;
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


    /**
     * TODO: 익명 파악해보기
     * @param inquiryId
     */
    public void recommendInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);
        Member member = securityReadService.getMember();

        recommend(inquiry, member);
        addRecommendCount(inquiry.getId());
    }

    public void deleteRecommendBoard(Long inquiryId) {
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(inquiry, member);

        inquiryRecommendRepository.deleteByInquiryIdAndMemberPublicId(inquiry.getId(), member.getPublicId());

        minusRecommendCount(inquiryId);
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
        InquiryRecommend recommend = InquiryRecommend.builder().inquiry(inquiry).member(member).build();
        inquiryRecommendRepository.save(recommend);
    }

    /**
     * recommendCount 증가
     */
    public void addRecommendCount(Long inquiryId) {
        inquiryCountReadService.findByInquiryId(inquiryId).addRecommendCount();
    }

    /**
     * recommendCount 감소
     */
    public void minusRecommendCount(Long inquiryId) {
        inquiryCountReadService.findByInquiryId(inquiryId).minusRecommendCount();
    }

    /**
     * commentCount 증가
     */
    public void addCommentCount(Long inquiryId) {
        inquiryCountReadService.findByInquiryId(inquiryId).addCommentCount();
    }

    /**
     * commentCount 감소
     */
    public void minusCommentCount(Long inquiryId, int count) {
        inquiryCountReadService.findByInquiryId(inquiryId).minusCommentCount(count);
    }
}
