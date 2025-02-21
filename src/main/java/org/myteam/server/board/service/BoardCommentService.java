package org.myteam.server.board.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.myteam.server.board.dto.request.BoardCommentSaveRequest;
import org.myteam.server.board.dto.request.BoardCommentUpdateRequest;
import org.myteam.server.board.repository.BoardCommentRecommendRepository;
import org.myteam.server.board.repository.BoardCommentRepository;
import org.myteam.server.board.repository.BoardReplyRecommendRepository;
import org.myteam.server.board.repository.BoardReplyRepository;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardReadService boardReadService;
    private final BoardCommentReadService boardCommentReadService;
    private final SecurityReadService securityReadService;
    private final MemberReadService memberReadService;
    private final BoardCountService boardCountService;
    private final BoardReplyReadService boardReplyReadService;
    private final BoardCommentRecommendReadService boardCommentRecommendReadService;

    private final BoardCommentRecommendRepository boardCommentRecommendRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardReplyRecommendRepository boardReplyRecommendRepository;
    private final BoardReplyRepository boardReplyRepository;

    private final BadWordFilter badWordFilter;
    private final S3Service s3Service;

    /**
     * 게시판 댓글 생성
     */
    @Transactional
    public BoardCommentResponse save(Long boardId, BoardCommentSaveRequest request, String createdIp) {
        Board board = boardReadService.findById(boardId);
        Member member = securityReadService.getMember();

        BoardComment boardComment = BoardComment.createBoardComment(board, member, request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), createdIp);

        boardCommentRepository.save(boardComment);

        boardCountService.addCommentCount(board.getId());

        boolean isRecommended = boardCommentRecommendReadService.isRecommended(boardComment.getId(),
                member.getPublicId());

        return BoardCommentResponse.createResponse(boardComment, member, isRecommended);
    }

    /**
     * 게시판 댓글 수정
     */
    @Transactional
    public BoardCommentResponse update(Long boardCommentId, BoardCommentUpdateRequest request) {
        Member member = securityReadService.getMember();
        BoardComment boardComment = boardCommentReadService.findById(boardCommentId);

        boardComment.verifyBoardCommentAuthor(boardComment, member);
        verifyBoardCommentImageAndRequestImage(boardComment.getImageUrl(), request.getImageUrl());

        boardComment.updateComment(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()));
        boardCommentRepository.save(boardComment);

        boolean isRecommended = boardCommentRecommendReadService.isRecommended(boardComment.getId(),
                member.getPublicId());

        return BoardCommentResponse.createResponse(boardComment, member, isRecommended);
    }

    /**
     * 게시판 댓글 삭제
     */
    @Transactional
    public void deleteBoardComment(Long boardCommentId) {
        UUID loginUser = securityReadService.getMember().getPublicId();

        Member member = memberReadService.findById(loginUser);
        BoardComment boardComment = boardCommentReadService.findById(boardCommentId);

        boardComment.verifyBoardCommentAuthor(boardComment, member);

        // 대댓글 삭제 (카운트도 포함)
        int minusCount = deleteBoardReply(boardComment.getId());
        // 댓글 추천 삭제
        boardCommentRecommendRepository.deleteByBoardCommentId(boardComment.getId());
        if (boardComment.getImageUrl() != null) {
            // S3 이미지 삭제
            s3Service.deleteFile(MediaUtils.getImagePath(boardComment.getImageUrl()));
        }
        // 댓글 삭제
        boardCommentRepository.deleteById(boardCommentId);

        // 댓글 카운트 감소
        boardCountService.minusCommentCount(boardComment.getBoard().getId(), minusCount + 1);
    }

    /**
     * 대댓글 삭제
     */
    private int deleteBoardReply(Long boardCommentId) {
        List<BoardReply> boardReplyList = boardReplyReadService.findByBoardCommentId(boardCommentId);
        for (BoardReply boardReply : boardReplyList) {
            // 대댓글 추천 삭제
            boardReplyRecommendRepository.deleteAllByBoardReplyId(boardReply.getId());
            if (boardReply.getImageUrl() != null) {
                // 대댓글 이미지 삭제
                s3Service.deleteFile(MediaUtils.getImagePath(boardReply.getImageUrl()));
            }
            // 대댓글 삭제
            boardReplyRepository.delete(boardReply);
        }
        return boardReplyList.size();
    }

    /**
     * 기존 이미지와 요청 이미지가 같지 않으면 삭제
     */
    private void verifyBoardCommentImageAndRequestImage(String boardCommentImageUrl, String requestImageUrl) {
        if (!boardCommentImageUrl.equals(requestImageUrl)) {
            s3Service.deleteFile(MediaUtils.getImagePath(requestImageUrl));
        }
    }
}