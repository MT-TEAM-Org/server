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
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardCommentController {

    private final BoardCommentService boardCommentService;
    private final BoardCommentReadService boardCommentReadService;

    /**
     * 게시판 댓글 생성
     */
    @PostMapping("/{boardId}/comment")
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
    @PutMapping("/comment/{boardCommentId}")
    public ResponseEntity<ResponseDto<BoardCommentResponse>> updateBoardComment(@PathVariable Long boardCommentId,
                                                                                @RequestBody @Valid BoardCommentUpdateRequest request) {
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 수정 성공", boardCommentService.update(boardCommentId, request)));
    }

    /**
     * 게시판 댓글 삭제
     */
    @DeleteMapping("/comment/{boardCommentId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoardComment(@PathVariable Long boardCommentId) {
        boardCommentService.deleteBoardComment(boardCommentId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시판 댓글 삭제 성공", null));
    }

    /**
     * 댓글 상세 조회 (대댓글 포함) -> 댓글 ID로 조회
     */
    @GetMapping("/comment/{boardCommentId}")
    public ResponseEntity<ResponseDto<BoardCommentResponse>> getBoardComment(@PathVariable Long boardCommentId,
                                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시판 댓글 조회 성공",
                boardCommentReadService.findByIdWithReply(boardCommentId, userDetails)));
    }

    /**
     * 게시판 댓글 목록 조회 (대댓글 포함) -> 게시글 ID로 조회
     */
    @GetMapping("/{boardId}/comment")
    public ResponseEntity<ResponseDto<BoardCommentListResponse>> getBoardComments(@PathVariable Long boardId,
                                                                                  @RequestParam BoardOrderType orderType,
                                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 목록 조회 성공",
                        boardCommentReadService.findByBoardId(boardId, orderType, userDetails)));
    }
}