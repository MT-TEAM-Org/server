package org.myteam.server.admin.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.service.AdminInquiryService;
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

import static org.myteam.server.admin.dto.AdminMemoRequestDto.AdminMemoInquiryRequest;
import static org.myteam.server.admin.dto.InquiryResponseDto.ResponseInquiryList;
import static org.myteam.server.admin.dto.InquiryResponseDto.ResponseInquiryListCond;
import static org.myteam.server.admin.dto.RequestInquiryDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/inquiry")
@Tag(name = "관리자 문의 관리", description = "관리자가 문의를 관리할수있는 api")
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;

    @Operation(summary = "조건에 따른 문의들 조회",
            description = "관리자가 문의들을 조건에 따라서 여러개 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<Page<ResponseInquiryListCond>>> getInquiryListCond(
            @RequestBody @Valid RequestInquiryListCond requestInquiryListCond, BindingResult bindingResult) {

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(), "ok",
                adminInquiryService.getInquiryListCond(requestInquiryListCond)
        ));
    }

    @Operation(summary = "회원이 작성한 문의 조회",
            description = "회원이 작성한 문의들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/member/list")
    public ResponseEntity<ResponseDto<Page<ResponseInquiryList>>> getInquiryListMember(
            @RequestBody @Valid RequestInquiryList requestInquiryList, BindingResult bindingResult) {

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(), "ok",
                adminInquiryService.getInquiryListMember(requestInquiryList)
        ));
    }

    @Operation(summary = "문의 상세조회",
            description = "회원이 작성한 문의에 대한 상세한 조회를 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/detail")
    public ResponseEntity<ResponseDto<ResponseInquiryDetail>> getInquiryDetail(
            @RequestBody @Valid RequestInquiryDetail requestInquiryDetail, BindingResult bindingResult) {

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(), "ok",
                adminInquiryService.getInquiryDetail(requestInquiryDetail)
        ));
    }

    @Operation(summary = "관리자 매모 추가.",
            description = "문의에 대한 관리자 메모 추가 및 답변이 메일로 전송됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/add/memo")
    public ResponseEntity<ResponseDto<String>> requestAddAdminMemo(
            @RequestBody @Valid AdminMemoInquiryRequest adminMemoRequest, BindingResult bindingResult) {
        adminInquiryService.sendInquiryAnswer(adminMemoRequest);
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(), "ok",
                "성공"));
    }

}
