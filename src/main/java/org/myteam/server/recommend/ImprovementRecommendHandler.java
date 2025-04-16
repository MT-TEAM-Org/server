package org.myteam.server.recommend;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementRecommend;
import org.myteam.server.improvement.repository.ImprovementRecommendRepository;
import org.myteam.server.improvement.repository.ImprovementRepository;
import org.myteam.server.improvement.service.ImprovementRecommendReadService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImprovementRecommendHandler implements RecommendHandler {
    private final ImprovementRecommendReadService improvementRecommendReadService;
    private final ImprovementRecommendRepository improvementRecommendRepository;
    private final ImprovementRepository improvementRepository;

    @Override
    public boolean supports(DomainType type) {
        return type.name().equalsIgnoreCase("improvement");
    }

    @Override
    public boolean isAlreadyRecommended(Long contentId, UUID userId) {
        // Redis Set 조회나 DB 조회
        return improvementRecommendReadService.isRecommended(contentId, userId);
    }

    @Override
    public void saveRecommendation(Long contentId, Member member) {
        // 저장 또는 큐에 넣기
        Improvement improvement = improvementRepository.findById(contentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_NOT_FOUND));
        ImprovementRecommend recommend = ImprovementRecommend.builder().improvement(improvement).member(member).build();
        improvementRecommendRepository.save(recommend);
    }

    @Override
    public void deleteRecommendation(Long contentId, UUID userId) {
        improvementRecommendRepository.deleteByImprovementIdAndMemberPublicId(contentId, userId);
    }
}