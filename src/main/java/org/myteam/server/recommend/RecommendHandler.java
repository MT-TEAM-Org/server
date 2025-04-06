package org.myteam.server.recommend;

import org.myteam.server.board.domain.Board;
import org.myteam.server.member.entity.Member;

import java.util.UUID;

public interface RecommendHandler {
    boolean supports(String content);
    boolean isAlreadyRecommended(Long contentId, UUID userId);
    void saveRecommendation(Long contentId, Member member);
    void deleteRecommendation(Long contentId, UUID userId);
}
