//package org.myteam.server.board.service;
//
//import java.util.UUID;
//import lombok.RequiredArgsConstructor;
//import org.myteam.server.board.domain.BoardComment;
//import org.myteam.server.board.repository.BoardCommentLockRepository;
//import org.myteam.server.board.repository.BoardCommentRecommendRepository;
//import org.myteam.server.global.exception.ErrorCode;
//import org.myteam.server.global.exception.PlayHiveException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class BoardCommentRecommendReadService {
//
//    private final BoardCommentRecommendRepository boardCommentRecommendRepository;
//    private final BoardCommentLockRepository boardCommentLockRepository;
//
//    public void confirmExistBoardRecommend(Long boardCommentId, UUID publicId) {
//        boardCommentRecommendRepository.findByBoardCommentIdAndMemberPublicId(boardCommentId, publicId)
//                .ifPresent(member -> {
//                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_COMMENT);
//                });
//    }
//
//    public boolean isAlreadyRecommended(Long boardCommentId, UUID publicId) {
//        if (!boardCommentRecommendRepository.findByBoardCommentIdAndMemberPublicId(boardCommentId, publicId)
//                .isPresent()) {
//            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
//        }
//        return true;
//    }
//
//    public boolean isRecommended(Long boardCommentId, UUID publicId) {
//        return boardCommentRecommendRepository.findByBoardCommentIdAndMemberPublicId(boardCommentId, publicId)
//                .isPresent();
//    }
//
//    public BoardComment findByIdLock(Long boardCommentId) {
//        return boardCommentLockRepository.findById(boardCommentId)
//                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_COMMENT_RECOMMEND_NOT_FOUND));
//    }
//}