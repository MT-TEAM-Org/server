package org.myteam.server.inquiry.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.service.InquiryAnswerReadService;
import org.myteam.server.inquiry.service.InquiryAnswerWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/inquiries/answers")
@RequiredArgsConstructor
public class InquiryAnswerController {

    private final InquiryAnswerWriteService inquiryAnswerWriteService;

    @PostMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<String>> addAnswer(@PathVariable Long inquiryId, @RequestBody String answerContent) {
        inquiryAnswerWriteService.createAnswer(inquiryId, answerContent);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "답변이 성공적으로 등록되었습니다.",
                null
        ));
    }

    @PutMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<String>> updateAnswer(@PathVariable Long inquiryId, @RequestBody String updateContent) {
        inquiryAnswerWriteService.updateAnswer(inquiryId, updateContent);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "답변이 성공적으로 수정되었습니다.",
                null
        ));
    }

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<ResponseDto<String>> deleteAnswer(@PathVariable Long inquiryId) {
        inquiryAnswerWriteService.deleteAnswer(inquiryId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "답변이 성공적으로 삭제되었습니다.",
                null
        ));
    }

}