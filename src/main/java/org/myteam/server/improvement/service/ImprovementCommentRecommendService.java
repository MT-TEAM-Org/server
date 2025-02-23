package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.improvement.domain.ImprovementComment;
import org.myteam.server.improvement.domain.ImprovementCommentRecommend;
import org.myteam.server.improvement.repository.ImprovementCommentRecommendRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImprovementCommentRecommendService {

    private final ImprovementCommentReadService improvementCommentReadService;
    private final SecurityReadService securityReadService;
    private final ImprovementCommentRecommendReadService improvementCommentRecommendReadService;
    private final ImprovementCommentRecommendRepository improvementCommentRecommendRepository;

    public void recommendImprovementComment(Long improvementCommentId) {
        ImprovementComment improvementComment = improvementCommentReadService.findById(improvementCommentId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(improvementComment, member);

        recommend(improvementComment, member);

        addRecommendCount(improvementComment.getId());
    }

    public void deleteRecommendImprovementComment(Long improvementCommentId) {
        ImprovementComment improvementComment = improvementCommentReadService.findById(improvementCommentId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(improvementComment, member);

        improvementCommentRecommendRepository.deleteByImprovementCommentIdAndMemberPublicId(
                improvementComment.getId(), member.getPublicId());

        minusRecommendCount(improvementCommentId);
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(ImprovementComment improvementComment, Member member) {
        improvementCommentRecommendReadService.isAlreadyRecommended(improvementComment.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(ImprovementComment improvementComment, Member member) {
        improvementCommentRecommendReadService.confirmExistImprovementCommentRecommend(improvementComment.getId(), member.getPublicId());
    }

    /**
     * 게시판 댓글 추천 생성
     */
    private void recommend(ImprovementComment improvementComment, Member member) {
        ImprovementCommentRecommend recommend = ImprovementCommentRecommend.createImprovementCommentRecommend(improvementComment, member);
        improvementCommentRecommendRepository.save(recommend);
    }

    /**
     * 게시판 댓글 추천수 증가
     */
    private void addRecommendCount(Long improvementCommentId) {
        ImprovementComment improvementComment = improvementCommentRecommendReadService.findById(improvementCommentId);
        improvementComment.addRecommendCount();
    }

    /**
     * 게시판 댓글 추천소 감소
     */
    private void minusRecommendCount(Long improvementCommentId) {
        ImprovementComment improvementComment = improvementCommentRecommendReadService.findById(improvementCommentId);
        improvementComment.minusRecommendCount();
    }
}
