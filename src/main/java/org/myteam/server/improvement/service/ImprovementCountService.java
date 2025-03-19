package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.service.CommentCountService;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementRecommend;
import org.myteam.server.improvement.repository.ImprovementRecommendRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImprovementCountService implements CommentCountService {

    private final SecurityReadService securityReadService;
    private final ImprovementReadService improvementReadService;
    private final ImprovementRecommendReadService improvementRecommendReadService;
    private final ImprovementRecommendRepository improvementRecommendRepository;
    private final ImprovementCountReadService improvementCountReadService;

    public void recommendImprovement(Long improvementId) {
        log.info("개선요청: {} 추천 요청", improvementId);
        Improvement improvement = improvementReadService.findById(improvementId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(improvement, member);

        recommend(improvement, member);

        addRecommendCount(improvement.getId());
        log.info("개선요청: {} 추천 성공", improvementId);
    }

    public void deleteRecommendImprovement(Long improvementId) {
        log.info("개선요청: {} 삭제 요청", improvementId);
        Improvement improvement = improvementReadService.findById(improvementId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(improvement, member);

        improvementRecommendRepository.deleteByImprovementIdAndMemberPublicId(improvement.getId(), member.getPublicId());

        minusRecommendCount(improvementId);
        log.info("개선요청: {} 삭제 성공", improvementId);
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(Improvement improvement, Member member) {
        improvementRecommendReadService.isAlreadyRecommended(improvement.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(Improvement improvement, Member member) {
        improvementRecommendReadService.confirmExistImprovementRecommend(improvement.getId(), member.getPublicId());
    }

    /**
     * 게시글 추천 생성
     */
    private void recommend(Improvement improvement, Member member) {
        ImprovementRecommend recommend = ImprovementRecommend.builder()
                .improvement(improvement)
                .member(member)
                .build();
        improvementRecommendRepository.save(recommend);
    }

    /**
     * recommendCount 증가
     */
    public void addRecommendCount(Long improvementId) {
        improvementCountReadService.findByImprovementId(improvementId).addRecommendCount();
    }

    /**
     * recommendCount 감소
     */
    public void minusRecommendCount(Long improvementId) {
        improvementCountReadService.findByImprovementId(improvementId).minusRecommendCount();
    }

    /**
     * commentCount 증가
     */
    @Override
    public void addCommentCount(Long improvementId) {
        improvementCountReadService.findByImprovementId(improvementId).addCommentCount();
    }

    /**
     * commentCount 감소 (1 감소)
     */
    @Override
    public void minusCommentCount(Long improvementId) {
        improvementCountReadService.findByImprovementId(improvementId).minusCommentCount();
    }

    /**
     * commentCount 감소 (본 댓글 + 대댓글)
     */
    @Override
    public void minusCommentCount(Long improvementId, int count) {
        improvementCountReadService.findByImprovementId(improvementId).minusCommentCount(count);
    }

    public void addViewCount(Long improvementId) {
        improvementCountReadService.findByImprovementId(improvementId).addViewCount();
    }
}
