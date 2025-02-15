package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.board.dto.reponse.BoardReplyResponse;
import org.myteam.server.board.dto.request.BoardReplySaveRequest;
import org.myteam.server.board.repository.BoardReplyRepository;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardReplyService {

    private final BoardCountService boardCountService;
    private final BoardReplyReadService boardReplyReadService;
    private final BoardCommentReadService boardCommentReadService;
    private final MemberReadService memberReadService;
    private final SecurityReadService securityReadService;
    private final S3Service s3Service;

    private final BoardReplyRepository boardReplyRepository;
    private final BadWordFilter badWordFilter;

    /**
     * 게시판 대댓글 생성
     */
    @Transactional
    public BoardReplyResponse save(Long boardCommentId, BoardReplySaveRequest request,
                                   String createdIp) {
        Member loginUser = securityReadService.getMember();
        BoardComment boardComment = boardCommentReadService.findById(boardCommentId);

        Member mentionedMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        BoardReply boardReply = BoardReply.createBoardReply(boardComment, loginUser, request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), createdIp, mentionedMember);
        boardReplyRepository.save(boardReply);

        boardCountService.addCommentCount(boardComment.getBoard().getId());

        return BoardReplyResponse.createResponse(boardReply, loginUser, mentionedMember);
    }

    /**
     * 게시판 대댓글 수정
     */
    @Transactional
    public BoardReplyResponse update(Long boardReplyId, BoardReplySaveRequest request) {
        Member loginUser = securityReadService.getMember();
        BoardReply boardReply = boardReplyReadService.findById(boardReplyId);

        verifyBoardReplyAuthor(boardReply, loginUser);
        verifyBoardReplyImageAndRequestImage(boardReply.getImageUrl(), request.getImageUrl());

        Member mentionedMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        boardReply.updateReply(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()),
                mentionedMember);
        boardReplyRepository.save(boardReply);

        return BoardReplyResponse.createResponse(boardReply, loginUser, mentionedMember);
    }

    /**
     * 게시판 대댓글 삭제
     */
    @Transactional
    public void delete(Long boardReplyId) {
        Member loginUser = securityReadService.getMember();
        BoardReply boardReply = boardReplyReadService.findById(boardReplyId);

        verifyBoardReplyAuthor(boardReply, loginUser);

        s3Service.deleteFile(getImagePath(boardReply.getImageUrl()));
        boardReplyRepository.delete(boardReply);

        boardCountService.minusCommentCount(boardReply.getBoardComment().getBoard().getId());
    }

    /**
     * 기존 이미지와 요청 이미지가 같지 않으면 삭제
     */
    private void verifyBoardReplyImageAndRequestImage(String boardReplyImageUrl, String requestImageUrl) {
        if (!boardReplyImageUrl.equals(requestImageUrl)) {
            s3Service.deleteFile(getImagePath(requestImageUrl));
        }
    }

    /**
     * path만 추출
     * TODO :: 운영에선 버킷 이름 수정 예정
     */
    public static String getImagePath(String url) {
        String target = "devbucket/";
        int index = url.indexOf(target);
        if (index != -1) {
            return url.substring(index + target.length());
        }
        return null;
    }

    /**
     * 작성자와 일치 하는지 검사 (어드민도 수정/삭제 허용)
     */
    private void verifyBoardReplyAuthor(BoardReply boardReply, Member member) {
        if (!boardReply.isAuthor(member) && !member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }
}