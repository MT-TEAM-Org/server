package org.myteam.server.admin.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.service.AdminImprovementService;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.AdminMemoImprovementRequest;
import static org.myteam.server.admin.dto.response.ImprovementResponseDto.*;
import static org.myteam.server.admin.dto.request.ImproveRequestDto.*;

@RestController
@RequestMapping("/api/admin/improve")
@RequiredArgsConstructor
@Tag(name = "관리자 개선 요청 관리", description = "관리자가 개선요청을 관리할수있는 api")
public class AdminImprovementController {

    private final AdminImprovementService improvementService;

    @Operation(summary = "조건에 따른 개선요청들 조회",
            description = "관리자가 개선요청을 조건에 따라서 여러개 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<Page<ResponseImprovement>>> getImproveListCond(
            @RequestBody @Valid RequestImprovementList requestImprovementList, BindingResult bindingResult) {
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "ok", improvementService.getImproveListCond(requestImprovementList)));
    }

    @Operation(summary = "회원이 만든 개선요청 사항을 가져옵니다.",
            description = "특정 회원이 작성한 개선요청들을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/member/list")
    public ResponseEntity<ResponseDto<Page<ResponseMemberImproveList>>> getImproveList(
            @RequestBody @Valid RequestMemberImproveList requestMemberImproveList, BindingResult bindingResult) {

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "ok", improvementService.getImproveListMember(requestMemberImproveList)));
    }

    @Operation(summary = "개선 요청 상세조회",
            description = "해당 개선 요청에 대한 상세한 정보들을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/detail")
    public ResponseEntity<ResponseDto<ResponseImprovementDetail>> getImproveDetail(
            @RequestBody @Valid RequestImprovementDetail requestImprovementDetail, BindingResult bindingResult) {
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "ok", improvementService.getImproveDetail(requestImprovementDetail)));
    }

    @Operation(summary = "관리자 메모 추가.",
            description = "개선 요청에 대한 관리자 메모를 추가 및 상태를 변경할수있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/add/memo")
    public ResponseEntity<ResponseDto<String>> requestAddAdminMemo(
            @RequestBody @Valid AdminMemoImprovementRequest adminMemoRequest, BindingResult bindingResult) {
        improvementService.addAdminMemo(adminMemoRequest);
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),
                "ok", "성공"));
    }
}
