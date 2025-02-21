package org.myteam.server.notice.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.notice.dto.request.NoticeRequest.*;
import org.myteam.server.notice.dto.response.NoticeResponse.*;
import org.myteam.server.notice.service.NoticeReadService;
import org.myteam.server.notice.service.NoticeService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final NoticeReadService noticeReadService;

    /**
     * 공지사항 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<NoticeSaveResponse>> saveNotice(@Valid @RequestBody NoticeSaveRequest noticeSaveRequest,
                                                                      HttpServletRequest request) {
        String clientIP = ClientUtils.getRemoteIP(request);
        NoticeSaveResponse response = noticeService.saveNotice(noticeSaveRequest, clientIP);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 생성 성공",
                response
        ));
    }

    /**
     * 공지사항 수정
     */
    @PutMapping("/{noticeId}")
    public ResponseEntity<ResponseDto<NoticeSaveResponse>> updateNotice(@Valid @RequestBody NoticeSaveRequest noticeSaveRequest,
                                                                      @PathVariable Long noticeId) {
        NoticeSaveResponse response = noticeService.updateNotice(noticeSaveRequest, noticeId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 수정 성공",
                response
        ));
    }

    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ResponseDto<Void>> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 삭제 성공",
                null
        ));
    }

    /**
     * 공지사항 상세 조회
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<ResponseDto<NoticeSaveResponse>> getNotice(@PathVariable Long noticeId,
                                                                     @AuthenticationPrincipal final CustomUserDetails userDetails) {
        NoticeSaveResponse response = noticeReadService.getNotice(noticeId, userDetails);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 조회 성공",
                response
        ));
    }

    /**
     * 공지사항 목록 조회
     */
    @GetMapping
    public ResponseEntity<ResponseDto<NoticeListResponse>> getNoticeList(@Valid @ModelAttribute NoticeSearchRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "게시글 목록 조회",
                noticeReadService.getNoticeList(request.toServiceRequest())
        ));
    }
}


