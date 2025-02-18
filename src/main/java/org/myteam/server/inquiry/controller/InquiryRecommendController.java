package org.myteam.server.inquiry.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.service.InquiryCountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/inquiry/recommend")
@RequiredArgsConstructor
@Tags(value = @Tag(name = "문의내용 추천 컨트롤러", description = "문의내용 추천 및 삭제 api"))
public class InquiryRecommendController {

    private final InquiryCountService inquiryCountService;

    /**
     * 문의내용 추천
     */
    @PostMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<String>> recommendInquiry(@PathVariable Long inquiryId) {
        inquiryCountService.recommendInquiry(inquiryId);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의내용 추천 성공",
                null
        ));
    }

    /**
     * 문의내용 추천 삭제
     */
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<String>> deleteInquiryRecommend(@PathVariable Long inquiryId) {
        inquiryCountService.deleteRecommendInquiry(inquiryId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "문의내용 추천 삭제",
                null
        ));
    }
}
