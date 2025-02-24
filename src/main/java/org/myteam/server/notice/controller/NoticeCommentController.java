package org.myteam.server.notice.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeCommentController {

    private final NoticeCommentService noticeCommentService;
    private final NoticeCommentReadService noticeCommentReadService;

    /**
     * 공지사항 댓글 생성
     */
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
    @GetMapping("/{noticeId}/comment")
    public ResponseEntity<ResponseDto<NoticeCommentListResponse>> getNoticeComments(@PathVariable Long noticeId,
                                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 댓글 목록 조회 성공",
                noticeCommentReadService.findByNoticeId(noticeId, userDetails)
        ));
    }
}
