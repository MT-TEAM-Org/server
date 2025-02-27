package org.myteam.server.improvement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.improvement.dto.request.ImprovementCommentRequest.*;
import org.myteam.server.improvement.dto.response.ImprovementCommentResponse.*;
import org.myteam.server.improvement.service.ImprovementReplyService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@RestController
@RequestMapping("/api/improvement/comment")
@RequiredArgsConstructor
@Tag(name = "개선 요청 대댓글 API", description = "개선 요청 대댓글 관련 API")
public class ImprovementReplyController {

    private final ImprovementReplyService improvementReplyService;

    /**
     * 개선 요청 대댓글 생성
     */
    @Operation(summary = "개선 요청 대댓글 생성", description = "특정 개선 요청의 댓글에 대댓글을 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 대댓글 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 개선 요청 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{improvementCommentId}/reply")
    public ResponseEntity<ResponseDto<ImprovementReplyResponse>> saveImprovementReply(@PathVariable Long improvementCommentId,
                                                                                      @Valid @RequestBody ImprovementReplySaveRequest improvementReplySaveRequest,
                                                                                      HttpServletRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선 요청 대댓글 저장 성공",
                improvementReplyService.saveReply(improvementCommentId, improvementReplySaveRequest, ClientUtils.getRemoteIP(request))
        ));
    }

    /**
     * 개선 요청 대댓글 수정
     */
    @Operation(summary = "개선 요청 대댓글 수정", description = "특정 개선 요청 대댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 대댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자 또는 관리자만 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 개선 요청 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reply/{improvementReplyId}")
    public ResponseEntity<ResponseDto<ImprovementReplyResponse>> updateImprovementReply(@PathVariable Long improvementReplyId,
                                                                                        @Valid @RequestBody ImprovementReplySaveRequest request) {

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선 요청 대댓글 수정 성공",
                improvementReplyService.update(improvementReplyId, request)
        ));
    }

    /**
     * 개선 요청 대댓글 삭제
     */
    @Operation(summary = "개선 요청 대댓글 삭제", description = "특정 개선 요청의 대댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 대댓글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "작성자 또는 관리자만 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 개선 요청 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/reply/{improvementReplyId}")
    public ResponseEntity<ResponseDto<Void>> deleteImprovementReply(@PathVariable Long improvementReplyId) {
        improvementReplyService.delete(improvementReplyId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선 요청 대댓글 삭제 성공",
                null
        ));
    }
}
