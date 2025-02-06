package org.myteam.server.inquiry.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquiryRequest;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryWriteService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryController {
    private final InquiryReadService inquiryReadService;
    private final InquiryWriteService inquiryWriteService;

    @PostMapping
    public ResponseEntity<ResponseDto<String>> createInquiry(@Valid @RequestBody InquiryRequest inquiryRequest,
                                                     HttpServletRequest request) {
        String clientIp = ClientUtils.getRemoteIP(request);
        String content = inquiryWriteService.createInquiry(inquiryRequest.getContent(), inquiryRequest.getMemberPublicId(), clientIp);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully upload inquiry",
                content
        ));
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDto<PageCustomResponse<InquiryResponse>>> getMyInquiries(@RequestParam UUID memberPublicId, PageInfoRequest pageInfoRequest) {
        PageCustomResponse<InquiryResponse> content = inquiryReadService.getInquiriesByMember(memberPublicId, pageInfoRequest);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully find inquiries",
                content
        ));
    }
}
