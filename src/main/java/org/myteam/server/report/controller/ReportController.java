package org.myteam.server.report.controller;

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
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.report.dto.request.ReportRequest.*;
import org.myteam.server.report.dto.response.ReportResponse.*;
import org.myteam.server.report.service.ReportService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "신고 API", description = "사용자의 신고 관련 API")
public class ReportController {

    private final ReportService reportService;

    /**
     * 🚀 신고 생성 API
     * @param request 신고 요청 데이터
     * @param httpRequest 클라이언트 요청 정보를 가져오기 위한 객체
     */
    @Operation(summary = "신고 생성", description = "사용자가 특정 대상(댓글, 게시글, 사용자 등)을 신고합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고가 정상적으로 접수되었습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "동일한 너무 많은 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDto<ReportSaveResponse>> reportContent(@Valid @RequestBody ReportSaveRequest request,
                                                                         HttpServletRequest httpRequest) {
        // 클라이언트 IP 가져오기
        String reportIp = ClientUtils.getRemoteIP(httpRequest);

        ReportSaveResponse reportResponse = reportService.reportContent(request, reportIp);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "신고가 정상적으로 접수 되었습니다.",
                reportResponse
        ));
    }

    /**
     * 🚀 신고 삭제 API
     * @brief: 일단 만들어 놓음
     * @param reportId 삭제할 신고 ID
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<ResponseDto<Void>> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "신고가 정상적으로 삭제 되었습니다.",
                null
        ));
    }
}

