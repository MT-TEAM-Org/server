package org.myteam.server.inquiry.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquiryCommentRequest.*;
import org.myteam.server.inquiry.dto.response.InquiryCommentResponse.*;
import org.myteam.server.inquiry.service.InquiryReplyService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/inquiry/comment")
@RequiredArgsConstructor
@Tags(value = @Tag(name = "문의내용 댓글 컨트롤러", description = "문의내용 댓글 api"))
public class InquiryReplyController {

    private final InquiryReplyService inquiryReplyService;

    /**
     * TODO: 확인해야함.
     * 문의사항 대댓글 생성
     */
    @Operation(summary = "문의사항 대댓글 생성", description = "특정 문의사항 댓글에 대댓글을 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의사항 대댓글 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 문의사항 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{inquiryCommentId}/reply")
    public ResponseEntity<ResponseDto<InquiryReplyResponse>> saveInquiryReply(@PathVariable Long inquiryCommentId,
                                                                              @Valid @RequestBody InquiryReplySaveRequest inquiryReplySaveRequest,
                                                                              HttpServletRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의사항 대댓글 저장 성공",
                inquiryReplyService.save(inquiryCommentId, inquiryReplySaveRequest, ClientUtils.getRemoteIP(request))
        ));
    }

    /**
     * 문의사항 대댓글 수정
     */
    @Operation(summary = "문의사항 대댓글 수정", description = "특정 문의사항 대댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의사항 대댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자 또는 관리자만 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 문의사항 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reply/{inquiryReplyId}")
    public ResponseEntity<ResponseDto<InquiryReplyResponse>> updateInquiryReply(@PathVariable Long inquiryReplyId,
                                                                                @Valid @RequestBody InquiryReplySaveRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의사항 대댓글 저장 성공",
                inquiryReplyService.update(inquiryReplyId, request)
        ));
    }

    /**
     * 문의사항 대댓글 삭제
     */
    @Operation(summary = "문의사항 대댓글 삭제", description = "특정 문의사항의 대댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의사항 대댓글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "작성자 또는 관리자만 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 문의사항 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/reply/{inquiryReplyId}")
    public ResponseEntity<ResponseDto<String>> deleteInquiryReply(@PathVariable Long inquiryReplyId) {
        inquiryReplyService.deleteReply(inquiryReplyId);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의사항 대댓글 삭제 성공",
                null
        ));
    }
}
