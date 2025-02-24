package org.myteam.server.inquiry.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.myteam.server.board.dto.request.BoardCommentSaveRequest;
import org.myteam.server.board.service.BoardCommentReadService;
import org.myteam.server.board.service.BoardCommentService;
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
    @GetMapping("/comment/{inquiryCommentId}")
    public ResponseEntity<ResponseDto<InquiryCommentResponse>> getInquiryComment(@PathVariable Long inquiryCommentId) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의사항 댓글 조회 성공",
                inquiryCommentReadService.findByIdWithReply(inquiryCommentId)));
    }
}
