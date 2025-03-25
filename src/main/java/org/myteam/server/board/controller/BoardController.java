package org.myteam.server.board.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.board.dto.request.BoardRequest;
import org.myteam.server.board.dto.request.BoardSearchRequest;
import org.myteam.server.board.service.BoardCountService;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardService;
import org.myteam.server.global.exception.ErrorResponse;
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
@Tag(name = "게시판 CRUD API", description = "게시글 생성, 수정, 상세 조회, 삭제 API")
public class BoardController {

    private final BoardService boardService;
    private final BoardCountService boardCountService;
    private final BoardReadService boardReadService;

    /**
     * 게시글 생성
     */
    @Operation(summary = "게시글 생성", description = "게시글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원, 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDto<BoardResponse>> saveBoard(
            @Valid @RequestBody final BoardRequest boardRequest,
            final HttpServletRequest request) {
        final String clientIP = ClientUtils.getRemoteIP(request);
        final BoardResponse response = boardService.saveBoard(boardRequest, clientIP);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 생성 성공", response));
    }

    /**
     * 게시글 수정
     */
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원, 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardResponse>> updateBoard(
            @PathVariable final Long boardId, @Valid @RequestBody final BoardRequest boardRequest) {
        final BoardResponse response = boardService.updateBoard(boardRequest, boardId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 수정 성공", response));
    }

    /**
     * 게시글 삭제
     */
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원, 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "s3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoard(@PathVariable final Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 삭제 성공", null));
    }

    /**
     * 게시글 상세 조회
     */
    @Operation(summary = "게시글 상세 조회", description = "게시글을 상세 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardResponse>> getBoard(@PathVariable final Long boardId,
                                                               @AuthenticationPrincipal final CustomUserDetails userDetails) {
        final BoardResponse response = boardService.getBoard(boardId, userDetails);
//        boardCountService.addViewCount(boardId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 조회 성공", response));
    }

    /**
     * 게시글 목록 조회
     */
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseDto<BoardListResponse>> getBoardList(
            @ModelAttribute @Valid BoardSearchRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 목록 조회",
                boardReadService.getBoardList(request.toServiceRequest())));
    }
}