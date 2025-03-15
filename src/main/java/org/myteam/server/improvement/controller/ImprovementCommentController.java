//package org.myteam.server.improvement.controller;
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
//import lombok.extern.slf4j.Slf4j;
//import org.myteam.server.global.exception.ErrorResponse;
//import org.myteam.server.global.web.response.ResponseDto;
//import org.myteam.server.improvement.domain.ImprovementOrderType;
//import org.myteam.server.improvement.dto.request.ImprovementCommentRequest.*;
//import org.myteam.server.improvement.dto.response.ImprovementCommentResponse.*;
//import org.myteam.server.improvement.service.ImprovementCommentReadService;
//import org.myteam.server.improvement.service.ImprovementCommentService;
//import org.myteam.server.util.ClientUtils;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/improvement")
//@RequiredArgsConstructor
//@Tag(name = "개선요청 댓글 API", description = "개선요청 댓글 등록 수정 삭제 조회 API")
//public class ImprovementCommentController {
//
//    private final ImprovementCommentService improvementCommentService;
//    private final ImprovementCommentReadService improvementCommentReadService;
//
//    /**
//     * 개선요청 댓글 생성
//     */
//    @Operation(summary = "개선 요청 댓글 생성", description = "개선 요청에 대해 댓글을 생성합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "댓글 저장 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "개선요청 글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @PostMapping("/{improvementId}/comment")
//    public ResponseEntity<ResponseDto<ImprovementCommentSaveResponse>> saveImprovementComment(@PathVariable Long improvementId,
//                                                                                              @Valid @RequestBody ImprovementCommentSaveRequest improvementCommentSaveRequest,
//                                                                                              HttpServletRequest httpServletRequest) {
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선요청 댓글 저장 성공",
//                improvementCommentService.save(improvementId, improvementCommentSaveRequest, ClientUtils.getRemoteIP(httpServletRequest))
//        ));
//    }
//
//    /**
//     * 개선요청 댓글 수정
//     */
//    @Operation(summary = "개선 요청 댓글 수정", description = "개선 요청에 대해 댓글을 수정합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "401", description = "작성자나 관리자만 댓글 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "개선요청 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @PutMapping("/comment/{improvementCommentId}")
//    public ResponseEntity<ResponseDto<ImprovementCommentSaveResponse>> updateImprovementComment(@PathVariable Long improvementCommentId,
//                                                                                                @Valid @RequestBody ImprovementCommentUpdateRequest improvementCommentUpdateRequest) {
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선요청 댓글 수정 성공",
//                improvementCommentService.update(improvementCommentId, improvementCommentUpdateRequest)
//        ));
//    }
//
//    /**
//     * 개선요청 댓글 삭제
//     */
//    @Operation(summary = "개선 요청 댓글 삭제", description = "개선 요청에 대해 댓글을 삭제합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "401", description = "작성자나 관리자만 댓글 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "개선요청 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @DeleteMapping("/comment/{improvementCommentId}")
//    public ResponseEntity<ResponseDto<Void>> deleteImprovementComment(@PathVariable Long improvementCommentId) {
//        improvementCommentService.deleteImprovementComment(improvementCommentId);
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선요청 댓글 삭제 성공",
//                null
//        ));
//    }
//
//    /**
//     * 댓글 상세 조회
//     */
//    @Operation(summary = "개선 요청 댓글 상세조회", description = "개선 요청에 대해 댓글을 상세조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "댓글 상세조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "개선요청 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @GetMapping("/comment/{improvementCommentId}")
//    public ResponseEntity<ResponseDto<ImprovementCommentSaveResponse>> getImprovementComment(@PathVariable Long improvementCommentId) {
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선요청 댓글 조회 성공",
//                improvementCommentReadService.findByIdWithReply(improvementCommentId)));
//    }
//
//    /**
//     * 개선요청 댓글 목록 조회
//     */
//    @Operation(summary = "개선 요청 댓글 목록조회", description = "개선 요청에 대해 댓글 목록을 조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "댓글 목록조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "개선요청 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @GetMapping("/{improvementId}/comment")
//    public ResponseEntity<ResponseDto<ImprovementCommentListResponse>> getImprovementComments(@PathVariable Long improvementId,
//                                                                                              @ModelAttribute ImprovementOrderType orderType) {
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선요청 댓글 목록 조회 성공",
//                improvementCommentReadService.findByImprovementId(improvementId, orderType)
//        ));
//    }
//
//    /**
//     * 개선요청 베스트 댓글 목록 조회
//     */
//    @Operation(summary = "개선요청 베스트 댓글 목록 조회 API", description = "개선요청 베스트 댓글을 조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "베스트 댓글 목록조회 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "404", description = "개선요청 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @GetMapping("/{improvementId}/best/comment")
//    public ResponseEntity<ResponseDto<List<ImprovementCommentSaveResponse>>> findBestByImprovementId(@PathVariable Long improvementId) {
//        return ResponseEntity.ok(new ResponseDto<>(
//                SUCCESS.name(),
//                "개선요청 베스트 댓글 조회 성공",
//                improvementCommentReadService.findBestByImprovementId(improvementId)
//        ));
//    }
//}
