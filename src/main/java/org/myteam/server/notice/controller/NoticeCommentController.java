package org.myteam.server.notice.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

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
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.notice.dto.request.NoticeCommentRequest.*;
import org.myteam.server.notice.dto.response.NoticeCommentResponse.*;
import org.myteam.server.notice.service.NoticeCommentReadService;
import org.myteam.server.notice.service.NoticeCommentService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
@Tag(name = "공지사항 댓글 API", description = "공지사항 댓글 등록 수정 삭제 조회 API")
public class NoticeCommentController {

    private final NoticeCommentService noticeCommentService;
    private final NoticeCommentReadService noticeCommentReadService;

    /**
     * 공지사항 댓글 생성
     */
    @Operation(summary = "공지사항 댓글 생성", description = "공지사항에 대해 댓글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "공지사항 글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/{noticeId}/comment")
    public ResponseEntity<ResponseDto<NoticeCommentSaveResponse>> saveNoticeComment(@PathVariable Long noticeId,
                                                                                    @Valid @RequestBody NoticeCommentSaveRequest noticeCommentSaveRequest,
                                                                                    HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 댓글 저장 성공",
                noticeCommentService.save(noticeId, noticeCommentSaveRequest, ClientUtils.getRemoteIP(httpServletRequest))
        ));
    }

    /**
     * 공지사항 댓글 수정
     */
    @Operation(summary = "공지사항 댓글 수정", description = "공지사항에 대해 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 댓글 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "공지사항 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping("/comment/{noticeCommentId}")
    public ResponseEntity<ResponseDto<NoticeCommentSaveResponse>> updateNoticeComment(@PathVariable Long noticeCommentId,
                                                                                      @Valid @RequestBody NoticeCommentUpdateRequest noticeCommentUpdateRequest) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 댓글 수정 성공",
                noticeCommentService.update(noticeCommentId, noticeCommentUpdateRequest)
        ));
    }

    /**
     * 공지사항 댓글 삭제
     */
    @Operation(summary = "공지사항 댓글 삭제", description = "공지사항에 대해 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "작성자나 관리자만 댓글 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "공지사항 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "S3 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/comment/{noticeCommentId}")
    public ResponseEntity<ResponseDto<Void>> deleteNoticeComment(@PathVariable Long noticeCommentId) {
        noticeCommentService.deleteNoticeComment(noticeCommentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 댓글 삭제 성공",
                null
        ));
    }

    /**
     * 댓글 상세 조회
     */
    @Operation(summary = "공지사항 댓글 상세조회", description = "공지사항에 대해 댓글을 상세조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 상세조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "공지사항 댓글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/comment/{noticeCommentId}")
    public ResponseEntity<ResponseDto<NoticeCommentSaveResponse>> getNoticeComment(@PathVariable Long noticeCommentId,
                                                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 댓글 조회 성공",
                noticeCommentReadService.findByIdWithReply(noticeCommentId, userDetails)));
    }

    /**
     * 공지사항 댓글 목록 조회
     */
    @Operation(summary = "공지사항 댓글 목록조회", description = "공지사항 대해 댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 목록조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "공지사항 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{noticeId}/comment")
    public ResponseEntity<ResponseDto<NoticeCommentListResponse>> getNoticeComments(@PathVariable Long noticeId,
                                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 댓글 목록 조회 성공",
                noticeCommentReadService.findByNoticeId(noticeId, userDetails)
        ));
    }

    /**
     * 공지사항 베스트 댓글 목록 조회
     */
    @Operation(summary = "공지사항 베스트 댓글 목록 조회 API", description = "공지사항 베스트 댓글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "베스트 댓글 목록조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "공지사항 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{noticeId}/best/comment")
    public ResponseEntity<ResponseDto<List<NoticeCommentSaveResponse>>> findBestByNoticeId(@PathVariable Long noticeId) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 베스트 댓글 조회 성공",
                noticeCommentReadService.findBestByNoticeId(noticeId)
        ));
    }
}
