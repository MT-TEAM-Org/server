package org.myteam.server.recommend;

import java.util.UUID;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;

public interface RecommendHandler {
    boolean supports(DomainType type);

    boolean isAlreadyRecommended(Long contentId, UUID userId);

    <T> void saveRecommendation(Long contentId, Member member);

    void deleteRecommendation(Long contentId, UUID userId);
}
