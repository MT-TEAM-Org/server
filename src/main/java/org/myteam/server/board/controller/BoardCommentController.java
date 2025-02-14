package org.myteam.server.board.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.dto.reponse.BoardCommentListResponse;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.myteam.server.board.dto.request.BoardCommentSaveRequest;
import org.myteam.server.board.dto.request.BoardCommentUpdateRequest;
import org.myteam.server.board.service.BoardCommentReadService;
import org.myteam.server.board.service.BoardCommentService;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board/{boardId}/comment")
@RequiredArgsConstructor
public class BoardCommentController {

    private final BoardCommentService boardCommentService;
    private final BoardCommentReadService boardCommentReadService;

    /**
     * 게시판 댓글 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<BoardCommentResponse>> saveBoardComment(
            @PathVariable Long boardId,
            @RequestBody @Valid BoardCommentSaveRequest boardCommentSaveRequest, HttpServletRequest request) {
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 저장 성공",
                        boardCommentService.save(boardId, boardCommentSaveRequest,
                                ClientUtils.getRemoteIP(request))));
    }

    /**
     * 게시판 댓글 수정
     */
    @PutMapping("/{boardCommentId}")
    public ResponseEntity<ResponseDto<BoardCommentResponse>> updateBoardComment(@PathVariable Long boardCommentId,
                                                                                @RequestBody @Valid BoardCommentUpdateRequest request) {
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 수정 성공", boardCommentService.update(boardCommentId, request)));
    }

    /**
     * 게시판 댓글 삭제
     */
    @DeleteMapping("/{boardCommentId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoardComment(@PathVariable Long boardCommentId) {
        boardCommentService.deleteBoardComment(boardCommentId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시판 댓글 삭제 성공", null));
    }

    /**
     * 게시판 댓글 목록 조회
     */
    @GetMapping
    public ResponseEntity<ResponseDto<BoardCommentListResponse>> getBoardComments(@PathVariable Long boardId,
                                                                                  @RequestParam BoardOrderType orderType) {
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 목록 조회 성공",
                        boardCommentReadService.findByBoardId(boardId, orderType)));
    }
}