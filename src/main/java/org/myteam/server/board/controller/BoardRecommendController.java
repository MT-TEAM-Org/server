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
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.myteam.server.board.service.BoardCommentRecommendService;
//import org.myteam.server.board.service.BoardCountService;
//import org.myteam.server.board.service.BoardReplyRecommendService;
//import org.myteam.server.global.exception.ErrorResponse;
//import org.myteam.server.global.web.response.ResponseDto;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/recommend/board")
//@RequiredArgsConstructor
//@Tag(name = "게시판 추천 API", description = "게시글 및 댓글, 대댓글 추천 관련 API")
//public class BoardRecommendController {
//
//    private final BoardCountService boardCountService;
//    private final BoardCommentRecommendService boardCommentRecommendService;
//    private final BoardReplyRecommendService boardReplyRecommendService;
//
//    /**
//     * 게시글 추천
//     */
//    @Operation(summary = "게시글 추천", description = "게시글을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시글 추천 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "409", description = "이미 추천되었음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/{boardId}")
//    public ResponseEntity<ResponseDto<Void>> recommendBoard(@PathVariable Long boardId) {
//        boardCountService.recommendBoard(boardId);
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시글 추천 성공", null));
//    }
//
//    /**
//     * 게시글 추천 삭제
//     */
//    @Operation(summary = "게시글 추천 삭제", description = "게시글 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시글 추천 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "회원, 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @DeleteMapping("/{boardId}")
//    public ResponseEntity<ResponseDto<Void>> deleteBoard(@PathVariable Long boardId) {
//        boardCountService.deleteRecommendBoard(boardId);
//        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게시글 추천 삭제 성공", null));
//    }
//
//    /**
//     * 게시판 댓글 추천
//     */
//    @Operation(summary = "게시판 댓글 추천", description = "게시글의 댓글을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 댓글 추천 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원, 게시판 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "409", description = "이미 추천되었음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/comment/{boardCommentId}")
//    public ResponseEntity<ResponseDto<Void>> recommendComment(@PathVariable Long boardCommentId) {
//        boardCommentRecommendService.recommendBoardComment(boardCommentId);
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 추천 성공", null));
//    }
//
//    /**
//     * 게시판 댓글 추천 삭제
//     */
//    @Operation(summary = "게시판 댓글 추천 삭제", description = "게시판 댓글의 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 댓글 추천 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "회원, 게시판 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @DeleteMapping("/comment/{boardCommentId}")
//    public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long boardCommentId) {
//        boardCommentRecommendService.deleteRecommendBoardComment(boardCommentId);
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 댓글 추천 삭제", null));
//    }
//
//    /**
//     * 게시판 대댓글 추천
//     */
//    @Operation(summary = "게시판 대댓글 추천", description = "게시판 댓글의 대댓글을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 대댓글 추천 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "게시판 대댓글, 회원이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "409", description = "이미 추천되었음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/reply/{boardReplyId}")
//    public ResponseEntity<ResponseDto<Void>> recommendReply(@PathVariable Long boardReplyId) {
//        boardReplyRecommendService.recommendBoardReply(boardReplyId);
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 추천 성공", null));
//    }
//
//    /**
//     * 게시판 대댓글 추천 삭제
//     */
//    @Operation(summary = "게시판 대댓글 추천 삭제", description = "게시판 대댓글의 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시판 대댓글 추천 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "회원, 게시판 대댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @DeleteMapping("/reply/{boardReplyId}")
//    public ResponseEntity<ResponseDto<Void>> deleteReply(@PathVariable Long boardReplyId) {
//        boardReplyRecommendService.deleteRecommendBoardReply(boardReplyId);
//        return ResponseEntity.ok(
//                new ResponseDto<>(SUCCESS.name(), "게시판 대댓글 추천 삭제", null));
//    }
//}