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
//import org.myteam.server.board.dto.reponse.BoardReplyResponse;
//import org.myteam.server.board.dto.request.BoardReplySaveRequest;
//import org.myteam.server.board.service.BoardReplyService;
//import org.myteam.server.global.exception.ErrorResponse;
//import org.myteam.server.global.web.response.ResponseDto;
//import org.myteam.server.util.ClientUtils;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/board/comment")
//@RequiredArgsConstructor
//@Tag(name = "게시판 대댓글 CRUD API", description = "게시판 대댓글 생성, 수정, 상세 조회, 삭제 API")
//public class BoardReplyController {
//
//    private final BoardReplyService boardReplyService;
//
//    /**
//     * 게시판 대댓글 생성
//     */
//    @Operation(summary = "게시판 대댓글 생성", description = "게시판 댓글에 대댓글을 생성합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 대댓글 생성 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @PostMapping("/{boardCommentId}/reply")
//    public ResponseEntity<ResponseDto<BoardReplyResponse>> saveBoardReply(@PathVariable Long boardCommentId,
//                                                                          @RequestBody @Valid BoardReplySaveRequest boardReplySaveRequest,
//                                                                          HttpServletRequest request) {
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 저장 성공",
//                        boardReplyService.save(boardCommentId, boardReplySaveRequest,
//                                ClientUtils.getRemoteIP(request))));
//    }
//
//    /**
//     * 게시판 대댓글 수정
//     */
//    @Operation(summary = "게시판 대댓글 수정", description = "게시판 댓글의 대댓글을 수정합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 대댓글 수정 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "403", description = "작성자 또는 관리자만 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 대댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @PutMapping("/reply/{boardReplyId}")
//    public ResponseEntity<ResponseDto<BoardReplyResponse>> updateBoardReply(@PathVariable Long boardReplyId,
//                                                                            @RequestBody @Valid BoardReplySaveRequest request) {
//
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 저장 성공", boardReplyService.update(boardReplyId, request))
//        );
//    }
//
//    /**
//     * 게시판 대댓글 삭제
//     */
//    @Operation(summary = "게시판 대댓글 삭제", description = "게시판 댓글의 대댓글을 삭제합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 대댓글 삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "403", description = "작성자 또는 관리자만 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 대댓글을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @DeleteMapping("/reply/{boardReplyId}")
//    public ResponseEntity<ResponseDto<Void>> deleteBoardReply(@PathVariable Long boardReplyId) {
//        boardReplyService.delete(boardReplyId);
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 삭제 성공", null));
//    }
//}