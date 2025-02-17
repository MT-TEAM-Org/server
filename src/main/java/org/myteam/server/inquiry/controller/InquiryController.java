package org.myteam.server.inquiry.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.request.InquiryRequest;
import org.myteam.server.inquiry.dto.response.InquiriesListResponse;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController {
    private final InquiryReadService inquiryReadService;
    private final InquiryService inquiryService;

    /**
     * 문의 내역 생성
     * @param inquiryRequest
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<ResponseDto<String>> createInquiry(@Valid @RequestBody InquiryRequest inquiryRequest,
                                                             HttpServletRequest request) {
        String clientIp = ClientUtils.getRemoteIP(request);
        String content = inquiryService.createInquiry(inquiryRequest.getContent(), inquiryRequest.getMemberPublicId(), clientIp);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully upload inquiry",
                content
        ));
    }

    /**
     * 문의내역 삭제
     */
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<Void>> deleteBoard(@PathVariable final Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "문의내역 삭제 성공", null));
    }

    /**
     * 문의내역 상세 조회
     */
    @GetMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<InquiryResponse>> getBoard(@PathVariable final Long inquiryId) {
        final InquiryResponse response = inquiryReadService.getInquiryById(inquiryId);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "문의 내역 조회 성공", response));
    }
}