package org.myteam.server.board.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.board.dto.request.BoardRequest;
import org.myteam.server.board.dto.request.BoardSaveRequest;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardService;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardReadService boardReadService;

    /**
     * 게시글 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<BoardResponse>> saveBoard(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody final BoardSaveRequest boardSaveRequest,
            final HttpServletRequest request) {
        final String clientIP = ClientUtils.getRemoteIP(request);
        final BoardResponse response = boardService.saveBoard(boardSaveRequest, userDetails, clientIP);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 생성 성공", response));
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardResponse>> updateBoard(
            @AuthenticationPrincipal final CustomUserDetails userDetails, @PathVariable final Long boardId,
            @Valid @RequestBody final BoardSaveRequest boardSaveRequest) {
        final BoardResponse response = boardService.updateBoard(boardSaveRequest, userDetails, boardId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 수정 성공", response));
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoard(@PathVariable final Long boardId,
                                                         @AuthenticationPrincipal final CustomUserDetails userDetails) {
        boardService.deleteBoard(boardId, userDetails);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 삭제 성공", null));
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardResponse>> getBoard(@PathVariable final Long boardId) {
        final BoardResponse response = boardService.getBoard(boardId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 조회 성공", response));
    }

    /**
     * 게시글 목록 조회
     */
    @GetMapping
    public ResponseEntity<ResponseDto<BoardListResponse>> getBoardList(@ModelAttribute @Valid BoardRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 목록 조회",
                boardReadService.getBoardList(request.toServiceRequest())));
    }
}