package org.myteam.server.recommend;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.repository.BoardRecommendRepository;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardRecommendReadService;
import org.myteam.server.member.entity.Member;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BoardRecommendHandler implements RecommendHandler {

    private final BoardRecommendReadService boardRecommendReadService;
    private final BoardRecommendRepository boardRecommendRepository;
    private final BoardReadService boardReadService;

    @Override
    public boolean supports(String content) {
        return content.equalsIgnoreCase("board");
    }

    @Override
    public boolean isAlreadyRecommended(Long contentId, UUID userId) {
        // Redis Set 조회나 DB 조회
        return boardRecommendReadService.isRecommended(contentId, userId);
    }

    @Override
    public void saveRecommendation(Long contentId, Member member) {
        // 저장 또는 큐에 넣기
        Board board = boardReadService.findById(contentId);
        BoardRecommend recommend = BoardRecommend.builder().board(board).member(member).build();
        boardRecommendRepository.save(recommend);
    }

    @Override
    public void deleteRecommendation(Long contentId, UUID userId) {
        boardRecommendRepository.deleteByBoardIdAndMemberPublicId(contentId, userId);
    }
}
