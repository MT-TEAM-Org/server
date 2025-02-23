package org.myteam.server.inquiry.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.inquiry.dto.request.InquiryReplySaveRequest;
import org.myteam.server.inquiry.dto.response.InquiryReplyResponse;
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
