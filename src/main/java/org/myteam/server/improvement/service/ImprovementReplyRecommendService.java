package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.improvement.domain.ImprovementReply;
import org.myteam.server.improvement.domain.ImprovementReplyRecommend;
import org.myteam.server.improvement.repository.ImprovementReplyRecommendRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImprovementReplyRecommendService {

    private final ImprovementReplyReadService improvementReplyReadService;
    private final SecurityReadService securityReadService;
    private final ImprovementReplyRecommendReadService improvementReplyRecommendReadService;
    private final ImprovementReplyRecommendRepository improvementReplyRecommendRepository;

    public void recommendImprovementReply(Long improvementReplyId) {
        ImprovementReply improvementReply = improvementReplyReadService.findById(improvementReplyId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(improvementReply, member);

        recommend(improvementReply, member);

        addRecommendCount(improvementReply.getId());
    }

    public void deleteRecommendImprovementReply(Long improvementReplyId) {
        ImprovementReply improvementReply = improvementReplyReadService.findById(improvementReplyId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(improvementReply, member);

        improvementReplyRecommendRepository.deleteByImprovementReplyIdAndMemberPublicId(improvementReply.getId(), member.getPublicId());

        minusRecommendCount(improvementReply.getId());
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(ImprovementReply improvementReply, Member member) {
        improvementReplyRecommendReadService.isAlreadyRecommended(improvementReply.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(ImprovementReply improvementReply, Member member) {
        improvementReplyRecommendReadService.confirmExistImprovementReply(improvementReply.getId(), member.getPublicId());
    }

    /**
     * 게시판 대댓글 추천 생성
     */
    private void recommend(ImprovementReply improvementReply, Member member) {
        ImprovementReplyRecommend improvementReplyRecommend = ImprovementReplyRecommend.createImprovementReplyRecommend(improvementReply, member);
        improvementReplyRecommendRepository.save(improvementReplyRecommend);
    }

    /**
     * 게시판 대댓글 추천수 증가
     */
    private void addRecommendCount(Long improvementReplyId) {
        ImprovementReply reply = improvementReplyRecommendReadService.findById(improvementReplyId);
        reply.addRecommendCount();
    }

    /**
     * 게시판 대댓글 추천소 감소
     */
    private void minusRecommendCount(Long improvementReplyId) {
        ImprovementReply reply = improvementReplyRecommendReadService.findById(improvementReplyId);
        reply.minusRecommendCount();
    }
}
