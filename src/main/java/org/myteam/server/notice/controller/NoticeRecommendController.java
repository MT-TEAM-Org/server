//package org.myteam.server.notice.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.myteam.server.global.exception.ErrorResponse;
//import org.myteam.server.global.web.response.ResponseDto;
//import org.myteam.server.notice.service.NoticeCommentRecommendService;
//import org.myteam.server.notice.service.NoticeCountService;
//import org.myteam.server.notice.service.NoticeReplyRecommendService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/recommend/notice")
//@RequiredArgsConstructor
//@Tag(name = "공지사항 추천 API", description = "공지사항 및 댓글, 대댓글 추천 관련 API")
//public class NoticeRecommendController {
//
//    private final NoticeCountService noticeCountService;
//    private final NoticeCommentRecommendService noticeCommentRecommendService;
//    private final NoticeReplyRecommendService noticeReplyRecommendService;
//
//    /**
//     * 공지사항 추천
//     */
//    @Operation(summary = "공지사항 추천", description = "특정 공지사항을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "공지사항 추천 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 공지사항 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "409", description = "이미 추천되었음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/{noticeId}")
//    public ResponseEntity<ResponseDto<Void>> recommendNotice(@PathVariable Long noticeId) {
//        noticeCountService.recommendNotice(noticeId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "공지사항 추천 성공",
//                null));
//    }
//
//    /**
//     * 공지사항 추천 삭제
//     */
//    @Operation(summary = "공지사항 추천 삭제", description = "특정 공지사항의 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "공지사항 추천 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 공지사항 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @DeleteMapping("/{noticeId}")
//    public ResponseEntity<ResponseDto<Void>> deleteNotice(@PathVariable Long noticeId) {
//        noticeCountService.deleteRecommendNotice(noticeId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "공지사항 추천 삭제 성공",
//                null
//        ));
//    }
//
//    /**
//     * 공지사항 댓글 추천
//     */
//    @Operation(summary = "공지사항 댓글 추천", description = "특정 공지사항의 댓글을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "공지사항 댓글 추천 성공"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 공지사항 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/comment/{noticeCommentId}")
//    public ResponseEntity<ResponseDto<Void>> recommendComment(@PathVariable Long noticeCommentId) {
//        noticeCommentRecommendService.recommendNoticeComment(noticeCommentId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "공지사항 댓글 추천 성공",
//                null
//        ));
//    }
//
//    /**
//     * 공지사항 댓글 추천 삭제
//     */
//    @Operation(summary = "공지사항 댓글 추천 삭제", description = "특정 공지사항의 댓글 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "공지사항 댓글 추천 삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "추천 내역을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 공지사항 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @DeleteMapping("/comment/{noticeCommentId}")
//    public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long noticeCommentId) {
//        noticeCommentRecommendService.deleteRecommendNoticeComment(noticeCommentId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "공지사항 댓글 추천 삭제",
//                null
//        ));
//    }
//
//    /**
//     * 공지사항 대댓글 추천
//     */
//    @Operation(summary = "공지사항 대댓글 추천", description = "특정 공지사항의 대댓글을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "공지사항 대댓글 추천 성공"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 공지사항 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/reply/{noticeReplyId}")
//    public ResponseEntity<ResponseDto<Void>> recommendReply(@PathVariable Long noticeReplyId) {
//        noticeReplyRecommendService.recommendNoticeReply(noticeReplyId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "공지사항 대댓글 추천 성공",
//                null
//        ));
//    }
//
//    /**
//     * 공지사항 대댓글 추천 삭제
//     */
//    @Operation(summary = "공지사항 대댓글 추천 삭제", description = "특정 공지사항의 대댓글 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "공지사항 대댓글 추천 삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "추천 내역을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 공지사항 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @DeleteMapping("/reply/{noticeReplyId}")
//    public ResponseEntity<ResponseDto<Void>> deleteReply(@PathVariable Long noticeReplyId) {
//        noticeReplyRecommendService.deleteRecommendNoticeReply(noticeReplyId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "공지사항 대댓글 추천 삭제",
//                null
//        ));
//    }
//}
