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
import org.myteam.server.board.repository.BoardCommentRepository;
import org.myteam.server.board.repository.BoardReplyRepository;
import org.myteam.server.chat.domain.BadWordFilter;
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
    private final S3Service s3Service;

    private final BoardCommentRepository boardCommentRepository;
    private final BoardReplyRepository boardReplyRepository;

    private final BadWordFilter badWordFilter;
    private final BoardReplyReadService boardReplyReadService;

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

        return BoardCommentResponse.createResponse(boardComment, member);
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

        return BoardCommentResponse.createResponse(boardComment, member);
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

        // S3 이미지 삭제
        s3Service.deleteFile(s3Service.getImagePath(boardComment.getImageUrl()));
        // 대댓글 삭제 (카운트도 포함)
        int minusCount = deleteBoardReply(boardComment.getId());
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
            s3Service.deleteFile(s3Service.getImagePath(boardReply.getImageUrl()));
            boardReplyRepository.delete(boardReply);
        }
        return boardReplyList.size();
    }

    /**
     * 기존 이미지와 요청 이미지가 같지 않으면 삭제
     */
    private void verifyBoardCommentImageAndRequestImage(String boardCommentImageUrl, String requestImageUrl) {
        if (!boardCommentImageUrl.equals(requestImageUrl)) {
            s3Service.deleteFile(s3Service.getImagePath(requestImageUrl));
        }
    }
}