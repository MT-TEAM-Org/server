package org.myteam.server.improvement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.improvement.domain.ImprovementOrderType;
import org.myteam.server.improvement.dto.request.ImprovementCommentRequest.*;
import org.myteam.server.improvement.dto.response.ImprovementCommentResponse.*;
import org.myteam.server.improvement.service.ImprovementCommentReadService;
import org.myteam.server.improvement.service.ImprovementCommentService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/improvement")
@RequiredArgsConstructor
public class ImprovementCommentController {

    private final ImprovementCommentService improvementCommentService;
    private final ImprovementCommentReadService improvementCommentReadService;

    /**
     * 개선요청 댓글 생성
     */
    @PostMapping("/{improvementId}/comment")
    public ResponseEntity<ResponseDto<ImprovementCommentSaveResponse>> saveImprovementComment(@PathVariable Long improvementId,
                                                                                              @Valid @RequestBody ImprovementCommentSaveRequest improvementCommentSaveRequest,
                                                                                              HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 댓글 저장 성공",
                improvementCommentService.save(improvementId, improvementCommentSaveRequest, ClientUtils.getRemoteIP(httpServletRequest))
        ));
    }

    /**
     * 개선요청 댓글 수정
     */
    @PutMapping("/comment/{improvementCommentId}")
    public ResponseEntity<ResponseDto<ImprovementCommentSaveResponse>> updateImprovementComment(@PathVariable Long improvementCommentId,
                                                                                                @Valid @RequestBody ImprovementCommentUpdateRequest improvementCommentUpdateRequest) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 댓글 수정 성공",
                improvementCommentService.update(improvementCommentId, improvementCommentUpdateRequest)
        ));
    }

    /**
     * 개선요청 댓글 삭제
     */
    @DeleteMapping("/comment/{improvementCommentId}")
    public ResponseEntity<ResponseDto<Void>> deleteImprovementComment(@PathVariable Long improvementCommentId) {
        improvementCommentService.deleteImprovementComment(improvementCommentId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 댓글 삭제 성공",
                null
        ));
    }

    /**
     * 댓글 상세 조회
     */
    @GetMapping("/comment/{improvementCommentId}")
    public ResponseEntity<ResponseDto<ImprovementCommentSaveResponse>> getImprovementComment(@PathVariable Long improvementCommentId,
                                                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 댓글 조회 성공",
                improvementCommentReadService.findByIdWithReply(improvementCommentId, userDetails)));
    }

    /**
     * 개선요청 댓글 목록 조회
     */
    @GetMapping("/{improvementId}/comment")
    public ResponseEntity<ResponseDto<ImprovementCommentListResponse>> getImprovementComments(@PathVariable Long improvementId,
                                                                                              @ModelAttribute ImprovementOrderType orderType,
                                                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 댓글 목록 조회 성공",
                improvementCommentReadService.findByImprovementId(improvementId, orderType, userDetails)
        ));
    }
}
