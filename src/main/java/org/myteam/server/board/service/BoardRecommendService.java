package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.repository.BoardRecommendRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardRecommendService {

    private final SecurityReadService securityReadService;
    private final BoardReadService boardReadService;
    private final BoardCountReadService boardCountReadService;
    private final BoardRecommendReadService boardRecommendReadService;

    private final BoardRecommendRepository boardRecommendRepository;

    @Transactional
    public void recommendBoard(Long boardId) {
        Board board = boardReadService.findById(boardId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(board, member);

        recommend(board, member);

        addRecommendCount(board.getId());
    }

    @Transactional
    public void deleteRecommendBoard(Long boardId) {

        Board board = boardReadService.findById(boardId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(board, member);

        boardRecommendRepository.deleteByBoardIdAndMemberPublicId(board.getId(), member.getPublicId());

        minusRecommendCount(boardId);
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(Board board, Member member) {
        boardRecommendReadService.isAlreadyRecommended(board.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(Board board, Member member) {
        boardRecommendReadService.confirmExistBoardRecommend(board.getId(), member.getPublicId());
    }

    /**
     * 게시글 추천 생성
     */
    private void recommend(Board board, Member member) {
        BoardRecommend recommend = BoardRecommend.builder().board(board).member(member).build();
        boardRecommendRepository.save(recommend);
    }

    /**
     * recommendCount 증가
     */
    public void addRecommendCount(Long boardId) {
        boardCountReadService.findByBoardId(boardId).addRecommendCount();
    }

    /**
     * recommendCount 감소
     */
    public void minusRecommendCount(Long boardId) {
        boardCountReadService.findByBoardId(boardId).minusRecommendCount();
    }
}