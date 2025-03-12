package org.myteam.server.notice.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.notice.dto.request.NoticeRequest.*;
import org.myteam.server.notice.dto.response.NoticeResponse.*;
import org.myteam.server.notice.service.NoticeCountService;
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
@Tag(name = "공지사항 API", description = "공지사항 등록, 수정, 삭제 및 조회 API")
public class NoticeController {

    private final NoticeService noticeService;
    private final NoticeReadService noticeReadService;
    private final NoticeCountService noticeCountService;

    /**
     * 공지사항 생성
     */
    @Operation(summary = "공지사항 생성", description = "새로운 공지사항을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "관리자만 공지사항 작성 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "공지사항 수정", description = "기존의 공지사항을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "관리자만 공지사항 작성 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 개선 요청이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "공지사항 삭제", description = "기존의 공지사항을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "관리자만 공지사항 작성 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 공지사항이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "공지사항 상세 조회", description = "특정 공지사항의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 공지사항이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{noticeId}")
    public ResponseEntity<ResponseDto<NoticeSaveResponse>> getNotice(@PathVariable Long noticeId) {
        noticeCountService.addViewCount(noticeId);
        NoticeSaveResponse response = noticeReadService.getNotice(noticeId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "공지사항 조회 성공",
                response
        ));
    }

    /**
     * 공지사항 목록 조회
     */
    @Operation(summary = "공지사항 목록 조회", description = "등록된 공지사항 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseDto<NoticeListResponse>> getNoticeList(@Valid @ModelAttribute NoticeSearchRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "게시글 목록 조회",
                noticeReadService.getNoticeList(request.toServiceRequest())
        ));
    }
}


