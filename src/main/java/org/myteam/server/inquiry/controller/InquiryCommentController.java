package org.myteam.server.inquiry.controller;

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
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquiryCommentRequest;
import org.myteam.server.inquiry.dto.request.InquiryCommentUpdateRequest;
import org.myteam.server.inquiry.dto.response.InquiryCommentListResponse;
import org.myteam.server.inquiry.dto.response.InquiryCommentResponse;
import org.myteam.server.inquiry.service.InquiryCommentReadService;
import org.myteam.server.inquiry.service.InquiryCommentService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiry")
@Tag(name = "문의 내역 댓글 API", description = "문의 내역 댓글 등록 수정 삭제 조회 API")
public class InquiryCommentController {

    private final InquiryCommentService inquiryCommentService;
    private final InquiryCommentReadService inquiryCommentReadService;

    /**
     * 문의 내역 댓글 생성
     * @param inquiryId
     * @param inquiryCommentRequest
     * @param request
     * @return
     */
    @Operation(summary = "문의내역 댓글 생성", description = "문의내역에 대해 댓글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "문의 내역 글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/{inquiryId}/comment")
    public ResponseEntity<ResponseDto<InquiryCommentResponse>> saveInquiryComment(
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryCommentRequest inquiryCommentRequest, HttpServletRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의내역 댓글 저장 성공",
                inquiryCommentService.save(inquiryId, inquiryCommentRequest, ClientUtils.getRemoteIP(request))
        ));
    }

    /**
     * 문의내역 댓글 수정
     * @param inquiryCommentId
     * @param request
     * @return
     */
    @Operation(summary = "문의내역 댓글 수정", description = "문의내역에 대해 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 댓글 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "문의 내역 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/comment/{inquiryCommentId}")
    public ResponseEntity<ResponseDto<InquiryCommentResponse>> updateInquiryComment(@PathVariable Long inquiryCommentId,
                                                                                    @Valid @RequestBody InquiryCommentUpdateRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의내역 댓글 수정 성공",
                inquiryCommentService.update(inquiryCommentId, request)
        ));
    }

    /**
     * 문의 내역 댓글 삭제
     * @param inquiryCommentId
     * @return
     */
    @Operation(summary = "문의내역 댓글 삭제", description = "문의내역에 대해 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 댓글 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "문의 내역 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/comment/{inquiryCommentId}")
    public ResponseEntity<ResponseDto<Void>> deleteInquiryComment(@PathVariable Long inquiryCommentId) {
        inquiryCommentService.deleteInquiryComment(inquiryCommentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의내역 댓글 삭제 성공",
                null
        ));
    }

    /**
     * 문의사항 댓글 목록 조회
     * @param inquiryId
     * @return
     */
    @Operation(summary = "문의 내역 댓글 목록조회", description = "문의 내역에 대해 댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 목록조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "개선요청 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{inquiryId}/comment")
    public ResponseEntity<ResponseDto<InquiryCommentListResponse>> getInquiryComments(@PathVariable Long inquiryId) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의내역 댓글 목록 조회 성공",
                inquiryCommentReadService.findByInquiryId(inquiryId)
        ));
    }

    /**
     * 댓글 상세 조회 (대댓글 포함) -> 댓글 ID로 조회
     */
    @Operation(summary = "문의 내역 댓글 상세조회", description = "문의 내역에 대해 댓글을 상세조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 상세조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "문의 내역 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/comment/{inquiryCommentId}")
    public ResponseEntity<ResponseDto<InquiryCommentResponse>> getInquiryComment(@PathVariable Long inquiryCommentId) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의사항 댓글 조회 성공",
                inquiryCommentReadService.findByIdWithReply(inquiryCommentId)));
    }
}
