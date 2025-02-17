package org.myteam.server.board.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.dto.reponse.BoardReplyResponse;
import org.myteam.server.board.dto.request.BoardReplySaveRequest;
import org.myteam.server.board.service.BoardReplyService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board/comment")
@RequiredArgsConstructor
public class BoardReplyController {

    private final BoardReplyService boardReplyService;

    /**
     * 게시판 대댓글 생성
     */
    @PostMapping("/{boardCommentId}/reply")
    public ResponseEntity<ResponseDto<BoardReplyResponse>> saveBoardReply(@PathVariable Long boardCommentId,
                                                                          @RequestBody @Valid BoardReplySaveRequest boardReplySaveRequest,
                                                                          HttpServletRequest request) {
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 저장 성공",
                        boardReplyService.save(boardCommentId, boardReplySaveRequest,
                                ClientUtils.getRemoteIP(request))));
    }

    /**
     * 게시판 대댓글 수정
     */
    @PutMapping("/reply/{boardReplyId}")
    public ResponseEntity<ResponseDto<BoardReplyResponse>> updateBoardReply(@PathVariable Long boardReplyId,
                                                                            @RequestBody @Valid BoardReplySaveRequest request) {

        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 저장 성공", boardReplyService.update(boardReplyId, request))
        );
    }

    /**
     * 게시판 대댓글 삭제
     */
    @DeleteMapping("/reply/{boardReplyId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoardReply(@PathVariable Long boardReplyId) {
        boardReplyService.delete(boardReplyId);
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 삭제 성공", null));
    }
}