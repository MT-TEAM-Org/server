package org.myteam.server.inquiry.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquiryRequest;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController {
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
}