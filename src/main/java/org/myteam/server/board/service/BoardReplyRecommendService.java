package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.board.domain.BoardReplyRecommend;
import org.myteam.server.board.repository.BoardReplyLockRepository;
import org.myteam.server.board.repository.BoardReplyRecommendRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardReplyRecommendService {

    private final BoardReplyReadService boardReplyReadService;
    private final SecurityReadService securityReadService;
    private final BoardReplyRecommendReadService boardReplyRecommendReadService;

    private final BoardReplyRecommendRepository boardReplyRecommendRepository;
    private final BoardReplyLockRepository boardReplyLockRepository;

    @Transactional
    public void recommendBoardReply(Long boardReplyId) {
        BoardReply boardReply = boardReplyReadService.findById(boardReplyId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(boardReply, member);

        recommend(boardReply, member);

        addRecommendCount(boardReply.getId());
    }

    @Transactional
    public void deleteRecommendBoardReply(Long boardReplyId) {
        BoardReply boardReply = boardReplyReadService.findById(boardReplyId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(boardReply, member);

        boardReplyRecommendRepository.deleteByBoardReplyIdAndMemberPublicId(boardReply.getId(), member.getPublicId());

        minusRecommendCount(boardReply.getId());
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(BoardReply boardReply, Member member) {
        boardReplyRecommendReadService.isAlreadyRecommended(boardReply.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(BoardReply boardReply, Member member) {
        boardReplyRecommendReadService.confirmExistBoardReply(boardReply.getId(), member.getPublicId());
    }

    /**
     * 게시판 대댓글 추천 생성
     */
    private void recommend(BoardReply boardReply, Member member) {
        BoardReplyRecommend boardReplyRecommend = BoardReplyRecommend.createBoardReplyRecommend(boardReply, member);
        boardReplyRecommendRepository.save(boardReplyRecommend);
    }

    /**
     * 게시판 대댓글 추천수 증가
     */
    private void addRecommendCount(Long boardReplyId) {
        BoardReply reply = boardReplyRecommendReadService.findByIdLock(boardReplyId);
        reply.addRecommendCount();
    }

    /**
     * 게시판 대댓글 추천소 감소
     */
    private void minusRecommendCount(Long boardReplyId) {
        BoardReply reply = boardReplyRecommendReadService.findByIdLock(boardReplyId);
        reply.minusRecommendCount();
    }
}
