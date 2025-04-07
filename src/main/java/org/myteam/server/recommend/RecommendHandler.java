package org.myteam.server.recommend;

import org.myteam.server.board.domain.Board;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;

import java.util.UUID;

public interface RecommendHandler {
    boolean supports(DomainType type);
    boolean isAlreadyRecommended(Long contentId, UUID userId);
    <T> void saveRecommendation(Long contentId, Member member);
    void deleteRecommendation(Long contentId, UUID userId);
}
