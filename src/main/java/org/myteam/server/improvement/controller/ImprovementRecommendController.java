//package org.myteam.server.improvement.controller;
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
//import org.myteam.server.improvement.service.ImprovementCommentRecommendService;
//import org.myteam.server.improvement.service.ImprovementCountService;
//import org.myteam.server.improvement.service.ImprovementReplyRecommendService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/recommend/improvement")
//@RequiredArgsConstructor
//@Tag(name = "개선 요청 추천 API", description = "개선 요청 및 댓글, 대댓글 추천 관련 API")
//public class ImprovementRecommendController {
//
//    private final ImprovementCountService improvementCountService;
//    private final ImprovementCommentRecommendService improvementCommentRecommendService;
//    private final ImprovementReplyRecommendService improvementReplyRecommendService;
//
//    /**
//     * 개선 요청 추천
//     */
//    @Operation(summary = "개선 요청 추천", description = "특정 개선 요청을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "개선 요청 추천 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 개선 요청 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "409", description = "이미 추천되었음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/{improvementId}")
//    public ResponseEntity<ResponseDto<Void>> recommendImprovement(@PathVariable Long improvementId) {
//        improvementCountService.recommendImprovement(improvementId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선 요청 추천 성공",
//                null));
//    }
//
//    /**
//     * 개선 요청 추천 삭제
//     */
//    @Operation(summary = "개선 요청 추천 삭제", description = "특정 개선 요청의 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "개선 요청 추천 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 개선 요청 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @DeleteMapping("/{improvementId}")
//    public ResponseEntity<ResponseDto<Void>> deleteImprovement(@PathVariable Long improvementId) {
//        improvementCountService.deleteRecommendImprovement(improvementId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선 요청 추천 삭제 성공",
//                null
//        ));
//    }
//
//    /**
//     * 개선 요청 댓글 추천
//     */
//    @Operation(summary = "개선 요청 댓글 추천", description = "특정 개선 요청의 댓글을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "개선 요청 댓글 추천 성공"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 개선 요청 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/comment/{improvementCommentId}")
//    public ResponseEntity<ResponseDto<Void>> recommendComment(@PathVariable Long improvementCommentId) {
//        improvementCommentRecommendService.recommendImprovementComment(improvementCommentId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선 요청 댓글 추천 성공",
//                null
//        ));
//    }
//
//    /**
//     * 개선 요청 댓글 추천 삭제
//     */
//    @Operation(summary = "개선 요청 댓글 추천 삭제", description = "특정 개선 요청의 댓글 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "개선 요청 댓글 추천 삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "추천 내역을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 개선 요청 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @DeleteMapping("/comment/{improvementCommentId}")
//    public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long improvementCommentId) {
//        improvementCommentRecommendService.deleteRecommendImprovementComment(improvementCommentId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선 요청 댓글 추천 삭제",
//                null
//        ));
//    }
//
//    /**
//     * 개선 요청 대댓글 추천
//     */
//    @Operation(summary = "개선 요청 대댓글 추천", description = "특정 개선 요청의 대댓글을 추천합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "개선 요청 대댓글 추천 성공"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 개선 요청 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/reply/{improvementReplyId}")
//    public ResponseEntity<ResponseDto<Void>> recommendReply(@PathVariable Long improvementReplyId) {
//        improvementReplyRecommendService.recommendImprovementReply(improvementReplyId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선 요청 대댓글 추천 성공",
//                null
//        ));
//    }
//
//    /**
//     * 개선 요청 대댓글 추천 삭제
//     */
//    @Operation(summary = "개선 요청 대댓글 추천 삭제", description = "특정 개선 요청의 대댓글 추천을 취소합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "개선 요청 대댓글 추천 삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "추천 내역을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "해당 개선 요청 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @DeleteMapping("/reply/{improvementReplyId}")
//    public ResponseEntity<ResponseDto<Void>> deleteReply(@PathVariable Long improvementReplyId) {
//        improvementReplyRecommendService.deleteRecommendImprovementReply(improvementReplyId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선 요청 대댓글 추천 삭제",
//                null
//        ));
//    }
//}
