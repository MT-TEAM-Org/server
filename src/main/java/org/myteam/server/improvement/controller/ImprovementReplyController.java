package org.myteam.server.improvement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class ImprovementReplyController {

    private final ImprovementReplyService improvementReplyService;

    /**
     * 개선요청 대댓글 생성
     */
    @PostMapping("/{improvementCommentId}/reply")
    public ResponseEntity<ResponseDto<ImprovementReplyResponse>> saveImprovementReply(@PathVariable Long improvementCommentId,
                                                                                      @Valid @RequestBody ImprovementReplySaveRequest improvementReplySaveRequest,
                                                                                      HttpServletRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 대댓글 저장 성공",
                improvementReplyService.saveReply(improvementCommentId, improvementReplySaveRequest, ClientUtils.getRemoteIP(request))
        ));
    }

    /**
     * 개선요청 대댓글 수정
     */
    @PutMapping("/reply/{improvementReplyId}")
    public ResponseEntity<ResponseDto<ImprovementReplyResponse>> updateImprovementReply(@PathVariable Long improvementReplyId,
                                                                                        @Valid @RequestBody ImprovementReplySaveRequest request) {

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 대댓글 수정 성공",
                improvementReplyService.update(improvementReplyId, request)
        ));
    }

    /**
     * 개선요청 대댓글 삭제
     */
    @DeleteMapping("/reply/{improvementReplyId}")
    public ResponseEntity<ResponseDto<Void>> deleteImprovementReply(@PathVariable Long improvementReplyId) {
        improvementReplyService.delete(improvementReplyId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 대댓글 삭제 성공",
                null
        ));
    }
}
