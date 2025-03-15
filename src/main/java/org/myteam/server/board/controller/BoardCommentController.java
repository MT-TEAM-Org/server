//package org.myteam.server.board.controller;
//
//import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.myteam.server.board.dto.reponse.BoardCommentListResponse;
//import org.myteam.server.board.dto.reponse.BoardCommentResponse;
//import org.myteam.server.board.dto.request.BoardCommentSaveRequest;
//import org.myteam.server.board.service.BoardCommentReadService;
//import org.myteam.server.board.service.BoardCommentService;
//import org.myteam.server.global.exception.ErrorResponse;
//import org.myteam.server.global.security.dto.CustomUserDetails;
//import org.myteam.server.global.web.response.ResponseDto;
//import org.myteam.server.util.ClientUtils;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/board")
//@RequiredArgsConstructor
//@Tag(name = "게시판 댓글 CRUD API", description = "게시판 댓글 생성, 수정, 상세 조회, 삭제 API")
//public class BoardCommentController {
//
//    private final BoardCommentService boardCommentService;
//    private final BoardCommentReadService boardCommentReadService;
//
//    /**
//     * 게시판 댓글 생성
//     */
//    @Operation(summary = "게시판 댓글 생성", description = "게시판의 댓글을 생성합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 댓글 생성 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/{boardId}/comment")
//    public ResponseEntity<ResponseDto<BoardCommentResponse>> saveBoardComment(
//            @PathVariable Long boardId,
//            @RequestBody @Valid BoardCommentSaveRequest boardCommentSaveRequest, HttpServletRequest request) {
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 저장 성공",
//                        boardCommentService.save(boardId, boardCommentSaveRequest,
//                                ClientUtils.getRemoteIP(request))));
//    }
//
//    /**
//     * 게시판 댓글 수정
//     */
//    @Operation(summary = "게시판 댓글 수정", description = "게시판의 댓글을 수정합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 댓글 수정 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 댓글 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 게시판 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PutMapping("/comment/{boardCommentId}")
//    public ResponseEntity<ResponseDto<BoardCommentResponse>> updateBoardComment(@PathVariable Long boardCommentId,
//                                                                                @RequestBody @Valid BoardCommentSaveRequest request) {
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 수정 성공", boardCommentService.update(boardCommentId, request)));
//    }
//
//    /**
//     * 게시판 댓글 삭제
//     */
//    @Operation(summary = "게시판 댓글 삭제", description = "게시판의 댓글을 삭제합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 댓글 삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 댓글 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 게시판 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @DeleteMapping("/comment/{boardCommentId}")
//    public ResponseEntity<ResponseDto<Void>> deleteBoardComment(@PathVariable Long boardCommentId) {
//        boardCommentService.deleteBoardComment(boardCommentId);
//        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시판 댓글 삭제 성공", null));
//    }
//
//    /**
//     * 게시판 댓글 상세 조회
//     */
//    @Operation(summary = "게시판 댓글 상세 조회", description = "게시판의 댓글을 상세 조회합니다. (대댓글 포함) -> 댓글 ID로 조회")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 댓글 상세 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 게시판 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @GetMapping("/comment/{boardCommentId}")
//    public ResponseEntity<ResponseDto<BoardCommentResponse>> getBoardComment(@PathVariable Long boardCommentId,
//                                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
//        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시판 댓글 조회 성공",
//                boardCommentReadService.findByIdWithReply(boardCommentId, userDetails)));
//    }
//
//    /**
//     * 게시판 댓글 목록 조회
//     */
//    @Operation(summary = "게시판 댓글 목록 조회", description = "게시판의 댓글을 목록 조회합니다. (대댓글 포함) -> 댓글 ID로 조회")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 댓글 목록 조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "게시판 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @GetMapping("/{boardId}/comment")
//    public ResponseEntity<ResponseDto<BoardCommentListResponse>> getBoardComments(@PathVariable Long boardId,
//                                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 목록 조회 성공",
//                        boardCommentReadService.findByBoardId(boardId, userDetails)));
//    }
//}