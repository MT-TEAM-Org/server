package org.myteam.server.board.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.myteam.server.board.dto.request.BoardCommentSaveRequest;
import org.myteam.server.board.dto.request.BoardCommentUpdateRequest;
import org.myteam.server.board.repository.BoardCommentRepository;
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
public class BoardCommentService {

    private final BoardReadService boardReadService;
    private final BoardCommentReadService boardCommentReadService;
    private final SecurityReadService securityReadService;
    private final MemberReadService memberReadService;
    private final BoardCountService boardCountService;
    private final S3Service s3Service;

    private final BoardCommentRepository boardCommentRepository;

    private final BadWordFilter badWordFilter;

    /**
     * 게시판 댓글 생성
     */
    @Transactional
    public BoardCommentResponse save(BoardCommentSaveRequest request, String createdIp) {
        Board board = boardReadService.findById(request.getBoardId());
        Member member = securityReadService.getMember();

        BoardComment boardComment = BoardComment.createBoardComment(board, member, request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), createdIp);

        boardCommentRepository.save(boardComment);

        boardCountService.addCommendCount(board.getId());

        return BoardCommentResponse.createResponse(boardComment, member);
    }

    /**
     * 게시판 댓글 수정
     */
    @Transactional
    public BoardCommentResponse update(Long boardCommentId, BoardCommentUpdateRequest request) {
        Member member = securityReadService.getMember();
        BoardComment boardComment = boardCommentReadService.findById(boardCommentId);

        verifyBoardCommentAuthor(boardComment, member);
        verifyBoardCommentImageAndRequestImage(boardComment.getImageUrl(), request.getImageUrl());

        boardComment.updateComment(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()));
        boardCommentRepository.save(boardComment);

        return BoardCommentResponse.createResponse(boardComment, member);
    }

    /**
     * 게시판 댓글 삭제
     */
    @Transactional
    public void deleteBoardComment(long boardCommentId) {
        UUID loginUser = securityReadService.getMember().getPublicId();

        Member member = memberReadService.findById(loginUser);
        BoardComment boardComment = boardCommentReadService.findById(boardCommentId);

        verifyBoardCommentAuthor(boardComment, member);

        s3Service.deleteFile(getImagePath(boardComment.getImageUrl()));
        boardCommentRepository.deleteById(boardCommentId);

        boardCountService.minusCommendCount(boardComment.getBoard().getId());
    }

    /**
     * 기존 이미지와 요청 이미지가 같지 않으면 삭제
     */
    private void verifyBoardCommentImageAndRequestImage(String boardCommentImageUrl, String requestImageUrl) {
        if (!boardCommentImageUrl.equals(requestImageUrl)) {
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
    private void verifyBoardCommentAuthor(BoardComment boardComment, Member member) {
        if (!boardComment.isAuthor(member)) {
            if (!member.isAdmin()) {
                throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
            }
        }
    }
}