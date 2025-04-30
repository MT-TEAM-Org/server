package org.myteam.server.improvement.controller;

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
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.improvement.dto.request.ImprovementRequest.ImprovementSaveRequest;
import org.myteam.server.improvement.dto.request.ImprovementRequest.ImprovementSearchRequest;
import org.myteam.server.improvement.dto.response.ImprovementResponse.ImprovementListResponse;
import org.myteam.server.improvement.dto.response.ImprovementResponse.ImprovementSaveResponse;
import org.myteam.server.improvement.service.ImprovementReadService;
import org.myteam.server.improvement.service.ImprovementService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/improvement")
@RequiredArgsConstructor
@Tag(name = "개선 요청 API", description = "개선 요청 등록, 수정, 삭제 및 조회 API")
public class ImprovementController {

    private final ImprovementService improvementService;
    private final ImprovementReadService improvementReadService;

    /**
     * 개선목록 생성
     */
    @Operation(summary = "개선 요청 생성", description = "새로운 개선 요청을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDto<ImprovementSaveResponse>> saveImprovement(
            @Valid @RequestBody ImprovementSaveRequest improvementSaveRequest,
            HttpServletRequest request) {
        String clientIP = ClientUtils.getRemoteIP(request);
        ImprovementSaveResponse response = improvementService.saveImprovement(improvementSaveRequest, clientIP);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 생성 성공",
                response
        ));
    }

    /**
     * 개선목록 수정
     */
    @Operation(summary = "개선 요청 수정", description = "기존의 개선 요청을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "작성자나 관리자만 수정 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 개선 요청이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<ImprovementSaveResponse>> updateImprovement(
            @Valid @RequestBody ImprovementSaveRequest improvementSaveRequest,
            @PathVariable Long improvementId) {
        ImprovementSaveResponse response = improvementService.updateImprovement(improvementSaveRequest, improvementId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 수정 성공",
                response
        ));
    }

    /**
     * 개선목록 삭제
     */
    @Operation(summary = "개선 요청 삭제", description = "기존의 개선 요청을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "작성자나 관리자만 삭제 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 개선 요청이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<Void>> deleteImprovement(@PathVariable Long improvementId) {
        improvementService.deleteImprovement(improvementId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 삭제 성공",
                null
        ));
    }

    /**
     * 개선목록 상세 조회
     */
    @Operation(summary = "개선 요청 상세 조회", description = "특정 개선 요청의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 개선 요청이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<ImprovementSaveResponse>> getImprovement(@PathVariable Long improvementId) {
        ImprovementSaveResponse response = improvementReadService.getImprovement(improvementId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 조회 성공",
                response
        ));
    }

    /**
     * 개선목록 목록 조회
     */
    @Operation(summary = "개선 요청 목록 조회", description = "등록된 개선 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseDto<ImprovementListResponse>> getImprovementList(
            @Valid @ModelAttribute ImprovementSearchRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 목록 조회",
                improvementReadService.getImprovementList(request.toServiceRequest())
        ));
    }

    /**
     * 개선요청 상태 업데이트(관리자)
     */
    @Operation(summary = "개선 요청 상태 변경", description = "기존의 개선 요청의 상태를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개선 요청 상태 변경 성공"),
            @ApiResponse(responseCode = "401", description = "작성자나 관리자만 상태변경 가능", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<ImprovementStatus>> updateImprovementStatus(@PathVariable Long improvementId, @RequestParam ImprovementStatus status) {
        ImprovementStatus updateStatus = improvementService.updateImprovementStatus(improvementId, status);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선요청 상태 업데이트 성공",
                updateStatus
        ));
    }
}
