package org.myteam.server.notice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class NoticeReplyController {

    private final NoticeReplyService noticeReplyService;

    /**
     * 공지사항 대댓글 생성
     */
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
