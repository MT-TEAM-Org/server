package org.myteam.server.board.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.service.BoardCommentRecommendService;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.board.service.BoardReplyRecommendService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/recommend/board")
@RequiredArgsConstructor
public class BoardRecommendController {

    private final BoardCountService boardCountService;
    private final BoardCommentRecommendService boardCommentRecommendService;
    private final BoardReplyRecommendService boardReplyRecommendService;

    /**
     * 게시글 추천
     */
    @PostMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Void>> recommendBoard(@PathVariable Long boardId) {
        boardCountService.recommendBoard(boardId);
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시글 추천 성공", null));
    }

    /**
     * 게시글 추천 삭제
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoard(@PathVariable Long boardId) {
        boardCountService.deleteRecommendBoard(boardId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 추천 삭제 성공", null));
    }

    /**
     * 게시판 댓글 추천
     */
    @PostMapping("/comment/{boardCommentId}")
    public ResponseEntity<ResponseDto<Void>> recommendComment(@PathVariable Long boardCommentId) {
        boardCommentRecommendService.recommendBoardComment(boardCommentId);
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 추천 성공", null));
    }

    /**
     * 게시판 댓글 추천 삭제
     */
    @DeleteMapping("/comment/{boardCommentId}")
    public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long boardCommentId) {
        boardCommentRecommendService.deleteRecommendBoardComment(boardCommentId);
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 추천 삭제", null));
    }

    /**
     * 게시판 댓글 추천
     */
    @PostMapping("/reply/{boardReplyId}")
    public ResponseEntity<ResponseDto<Void>> recommendReply(@PathVariable Long boardReplyId) {
        boardReplyRecommendService.recommendBoardReply(boardReplyId);
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 추천 성공", null));
    }

    /**
     * 게시판 댓글 추천 삭제
     */
    @DeleteMapping("/reply/{boardReplyId}")
    public ResponseEntity<ResponseDto<Void>> deleteReply(@PathVariable Long boardReplyId) {
        boardReplyRecommendService.deleteRecommendBoardReply(boardReplyId);
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 추천 삭제", null));
    }
}