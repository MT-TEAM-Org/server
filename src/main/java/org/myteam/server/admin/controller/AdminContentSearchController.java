package org.myteam.server.admin.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.service.ContentSearchService;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.*;
import static org.myteam.server.admin.dto.request.ContentRequestDto.*;
import static org.myteam.server.admin.dto.response.ResponseContentDto.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "관리자 게시물,댓글 API", description = "관리자가 회원을 관리할 수 있는 API")
@RequestMapping("/api/admin/content")
public class AdminContentSearchController {


    private final ContentSearchService contentSearchService;
    @Operation(summary = "댓글,게시글의 상세조회",
            description = "관리자가 특정 댓,게시글의 정보를 상회조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/detail")
    public ResponseEntity<ResponseDto<ResponseDetail>> getResponseDetail(
            @Parameter(description = "조회 하고자 하는 contentId입니다 필수입니다.") @RequestParam(name = "contentId",required = true)Long contentId,
            @Parameter(description = "조회 하고자 하는 신고의타입입니다. COMMENT,BOARD 중 1개택.")@RequestParam(name = "staticDateType",required = true) StaticDataType staticDataType,
            @Parameter(description = "reportId입니다. 관리자단 대시보드의 알림,최신 데이터에서 넘어올때 넣어주세요")@RequestParam(name = "reportId",required = false)Long reportId,
            @Parameter(description = "읽었는지 안읽었는지 체크하는 값입니다.관리자단 대시보드의 알림,최신데이터에서 넘어올때 넣어주세요")@RequestParam(name = "readCheck",required = false)String readCheck
    ){
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"ok",
                contentSearchService.getContentDetail(contentId,staticDataType,reportId,readCheck)));
    }
    @Operation(summary = "댓글,게시글 리스트 조회",
            description = "관리자가 제공한 조건에따라 댓,게시글의 리스트를 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<Page<ResponseContentSearch>>> getContentData(@RequestBody @Valid RequestContentData requestContentData){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"ok",
                contentSearchService.getContentList(requestContentData)));
    }
    @Operation(summary = "신고 리스트 조회",
            description = "댓글 혹은 게시글의 신고 리스트를 조회해서 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/reportList")
    public ResponseEntity<ResponseDto<Page<ResponseReportList>>> getContentDetail(
            @Parameter(description = "조회 하고자 하는 contentId입니다 필수입니다.") @RequestParam(name = "contentId",required = true)Long contentId,
            @Parameter(description = "조회 하고자 하는 신고의타입입니다. COMMENT,BOARD 중 1개택.")@RequestParam(name = "staticDateType",required = true) StaticDataType staticDataType,
            @Parameter(description = "offset값입니다. 처음은 1부터 시작해주세요.")@RequestParam(name = "page",required = true)@Size(min=1,message = "page값은 1부터 시작입니다.")Integer page
    ,BindingResult bindingResult){

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"ok",
                contentSearchService.getReportList(contentId,staticDataType,page)));
    }
    @Operation(summary = "관리자 메모 추가",
            description = "댓글,게시글에 대한 관리자 메모를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/addAdminMemo")
    public ResponseEntity<ResponseDto<String>> addAdminMemo(@RequestBody @Valid AdminMemoContentRequest adminMemoContentRequest
    , BindingResult bindingResult){

        contentSearchService.addAdminMemo(adminMemoContentRequest);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SUCCESS.name(),"ok",
                "ok"));
    }
}
