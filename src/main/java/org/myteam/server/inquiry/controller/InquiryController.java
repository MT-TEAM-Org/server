package org.myteam.server.inquiry.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquiryFindRequest;
import org.myteam.server.inquiry.dto.request.InquiryRequest;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryWriteService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * TODO: 차후 API로 사용하지 않고 마이 페이지에서 InquiryReadService를 호출하는 식으로 진행
     */
    @GetMapping("/my")
    public ResponseEntity<ResponseDto<PageCustomResponse<InquiryResponse>>> getMyInquiries(@ModelAttribute @Valid InquirySearchRequest inquirySearchRequest) {
        PageCustomResponse<InquiryResponse> content = inquiryReadService.getInquiriesByMember(inquirySearchRequest);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully find inquiries",
                content
        ));
    }
}
