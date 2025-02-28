package org.myteam.server.notice.controller;

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
import org.myteam.server.notice.dto.request.NoticeCommentRequest.*;
import org.myteam.server.notice.dto.response.NoticeCommentResponse.*;
import org.myteam.server.notice.service.NoticeReplyService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@RestController
@RequestMapping("/api/notice/comment")
@RequiredArgsConstructor
@Tag(name = "공지사항 대댓글 API", description = "공지사항 대댓글 관련 API")
public class NoticeReplyController {

    private final NoticeReplyService noticeReplyService;

    /**
     * 공지사항 대댓글 생성
     */
    @Operation(summary = "공지사항 대댓글 생성", description = "특정 공지사항의 댓글에 대댓글을 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 대댓글 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 공지사항 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{noticeCommentId}/reply")
    public ResponseEntity<ResponseDto<NoticeReplyResponse>> saveNoticeReply(@PathVariable Long noticeCommentId,
                                                                            @Valid @RequestBody NoticeReplySaveRequest noticeReplySaveRequest,
                                                                            HttpServletRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 대댓글 저장 성공",
                noticeReplyService.saveReply(noticeCommentId, noticeReplySaveRequest, ClientUtils.getRemoteIP(request))
        ));
    }

    /**
     * 공지사항 대댓글 수정
     */
    @Operation(summary = "공지사항 대댓글 수정", description = "특정 공지사항 대댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 대댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "작성자 또는 관리자만 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 공지사항 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/reply/{noticeReplyId}")
    public ResponseEntity<ResponseDto<NoticeReplyResponse>> updateNoticeReply(@PathVariable Long noticeReplyId,
                                                                              @Valid @RequestBody NoticeReplySaveRequest request) {

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 대댓글 저장 성공",
                noticeReplyService.update(noticeReplyId, request)
        ));
    }

    /**
     * 공지사항 대댓글 삭제
     */
    @Operation(summary = "공지사항 대댓글 삭제", description = "특정 공지사항의 대댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 대댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "작성자 또는 관리자만 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 공지사항 대댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/reply/{noticeReplyId}")
    public ResponseEntity<ResponseDto<Void>> deleteNoticeReply(@PathVariable Long noticeReplyId) {
        noticeReplyService.delete(noticeReplyId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 대댓글 삭제 성공",
                null
        ));
    }
}
