//package org.myteam.server.board.service;
//
//import lombok.RequiredArgsConstructor;
//import org.myteam.server.board.domain.BoardComment;
//import org.myteam.server.board.domain.BoardReply;
//import org.myteam.server.board.dto.reponse.BoardReplyResponse;
//import org.myteam.server.board.dto.request.BoardReplySaveRequest;
//import org.myteam.server.board.repository.BoardReplyRecommendRepository;
//import org.myteam.server.board.repository.BoardReplyRepository;
//import org.myteam.server.chat.domain.BadWordFilter;
//import org.myteam.server.global.util.upload.MediaUtils;
//import org.myteam.server.member.entity.Member;
//import org.myteam.server.member.service.MemberReadService;
//import org.myteam.server.member.service.SecurityReadService;
//import org.myteam.server.upload.service.S3Service;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class BoardReplyService {
//
//    private final BoardCountService boardCountService;
//    private final BoardReplyReadService boardReplyReadService;
//    private final BoardCommentReadService boardCommentReadService;
//    private final MemberReadService memberReadService;
//    private final BoardReplyRecommendReadService boardReplyRecommendReadService;
//    private final SecurityReadService securityReadService;
//    private final S3Service s3Service;
//
//    private final BoardReplyRepository boardReplyRepository;
//    private final BoardReplyRecommendRepository boardReplyRecommendRepository;
//    private final BadWordFilter badWordFilter;
//
//    /**
//     * 게시판 대댓글 생성
//     */
//    @Transactional
//    public BoardReplyResponse save(Long boardCommentId, BoardReplySaveRequest request,
//                                   String createdIp) {
//        Member loginUser = securityReadService.getMember();
//        BoardComment boardComment = boardCommentReadService.findById(boardCommentId);
//
//        Member mentionedMember = request.getMentionedPublicId() != null ?
//                memberReadService.findById(request.getMentionedPublicId()) : null;
//
//        BoardReply boardReply = BoardReply.createBoardReply(boardComment, loginUser, request.getImageUrl(),
//                badWordFilter.filterMessage(request.getComment()), createdIp, mentionedMember);
//        boardReplyRepository.save(boardReply);
//
//        boardCountService.addCommentCount(boardComment.getBoard().getId());
//
//        boolean isRecommended = boardReplyRecommendReadService.isRecommended(boardReply.getId(),
//                loginUser.getPublicId());
//
//        return BoardReplyResponse.createResponse(boardReply, loginUser, mentionedMember, isRecommended);
//    }
//
//    /**
//     * 게시판 대댓글 수정
//     */
//    @Transactional
//    public BoardReplyResponse update(Long boardReplyId, BoardReplySaveRequest request) {
//        Member loginUser = securityReadService.getMember();
//        BoardReply boardReply = boardReplyReadService.findById(boardReplyId);
//
//        boardReply.verifyBoardReplyAuthor(boardReply, loginUser);
//        verifyBoardReplyImageAndRequestImage(boardReply.getImageUrl(), request.getImageUrl());
//
//        Member mentionedMember = request.getMentionedPublicId() != null ?
//                memberReadService.findById(request.getMentionedPublicId()) : null;
//
//        boardReply.updateReply(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()),
//                mentionedMember);
//        boardReplyRepository.save(boardReply);
//
//        boolean isRecommended = boardReplyRecommendReadService.isRecommended(boardReply.getId(),
//                loginUser.getPublicId());
//
//        return BoardReplyResponse.createResponse(boardReply, loginUser, mentionedMember, isRecommended);
//    }
//
//    /**
//     * 게시판 대댓글 삭제
//     */
//    @Transactional
//    public void delete(Long boardReplyId) {
//        Member loginUser = securityReadService.getMember();
//        BoardReply boardReply = boardReplyReadService.findById(boardReplyId);
//
//        boardReply.verifyBoardReplyAuthor(boardReply, loginUser);
//
//        // 대댓글 추천 삭제
//        boardReplyRecommendRepository.deleteAllByBoardReplyId(boardReply.getId());
//        if (boardReply.getImageUrl() != null) {
//            // 대댓글 이미지 삭제
//            s3Service.deleteFile(MediaUtils.getImagePath(boardReply.getImageUrl()));
//        }
//        // 대댓글 삭제
//        boardReplyRepository.delete(boardReply);
//        // 게시글 댓글 카운트 감소
//        boardCountService.minusCommentCount(boardReply.getBoardComment().getBoard().getId());
//    }
//
//    /**
//     * 기존 이미지와 요청 이미지가 같지 않으면 삭제
//     */
//    private void verifyBoardReplyImageAndRequestImage(String boardReplyImageUrl, String requestImageUrl) {
//        if (!boardReplyImageUrl.equals(requestImageUrl)) {
//            s3Service.deleteFile(MediaUtils.getImagePath(requestImageUrl));
//        }
//    }
//}