package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.domain.BoardCommentRecommend;
import org.myteam.server.board.repository.BoardCommentLockRepository;
import org.myteam.server.board.repository.BoardCommentRecommendRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardCommentRecommendService {

    private final BoardCommentReadService boardCommentReadService;
    private final SecurityReadService securityReadService;
    private final BoardCommentRecommendReadService boardCommentRecommendReadService;

    private final BoardCommentRecommendRepository boardCommentRecommendRepository;
    private final BoardCommentLockRepository boardCommentLockRepository;

    @Transactional
    public void recommendBoardComment(Long boardCommentId) {
        BoardComment boardComment = boardCommentReadService.findById(boardCommentId);
        Member member = securityReadService.getMember();

        verifyMemberAlreadyRecommend(boardComment, member);

        recommend(boardComment, member);

        addRecommendCount(boardComment.getId());
    }

    @Transactional
    public void deleteRecommendBoardComment(Long boardCommentId) {
        BoardComment boardComment = boardCommentReadService.findById(boardCommentId);
        Member member = securityReadService.getMember();

        isAlreadyRecommended(boardComment, member);

        boardCommentRecommendRepository.deleteByBoardCommentIdAndMemberPublicId(boardComment.getId(),
                member.getPublicId());

        minusRecommendCount(boardCommentId);
    }

    /**
     * 추천이 되어있는 상태인지 검사
     */
    private void isAlreadyRecommended(BoardComment boardComment, Member member) {
        boardCommentRecommendReadService.isAlreadyRecommended(boardComment.getId(), member.getPublicId());
    }

    /**
     * 이미 추천한 상태인지 검사
     */
    private void verifyMemberAlreadyRecommend(BoardComment boardComment, Member member) {
        boardCommentRecommendReadService.confirmExistBoardRecommend(boardComment.getId(), member.getPublicId());
    }

    /**
     * 게시판 댓글 추천 생성
     */
    private void recommend(BoardComment boardComment, Member member) {
        BoardCommentRecommend recommend = BoardCommentRecommend.builder()
                .boardComment(boardComment)
                .member(member)
                .build();
        boardCommentRecommendRepository.save(recommend);
    }

    /**
     * 게시판 댓글 추천수 증가
     */
    private void addRecommendCount(Long boardCommentId) {
        BoardComment comment = boardCommentLockRepository.findById(boardCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_COMMENT_RECOMMEND_NOT_FOUND));

        comment.addRecommendCount();
    }

    /**
     * 게시판 댓글 추천소 감소
     */
    private void minusRecommendCount(Long boardCommentId) {
        BoardComment comment = boardCommentLockRepository.findById(boardCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_COMMENT_RECOMMEND_NOT_FOUND));

        comment.minusRecommendCount();
    }
}