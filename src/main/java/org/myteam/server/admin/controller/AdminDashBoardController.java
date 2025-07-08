package org.myteam.server.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.dto.AdminDashBorad.RequestStatic;
import org.myteam.server.admin.service.AdminDashBoardService;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.myteam.server.admin.dto.AdminDashBorad.*;

@RestController
@RequestMapping("/api/admin/data")
@RequiredArgsConstructor
@Tag(name = "관리자 대시보드 데이터 조회 api", description = "각 종류별 기간별 통계 데이터 및 각 종류별 최신 데이터를" +
        "가져오는 api입니다.")
public class AdminDashBoardController {


    private final AdminDashBoardService adminDashBoardService;


    @Operation(summary = "통계 데이터 가져오기", description = "종류,기간별 통계 데이터를 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/static")
    public ResponseEntity<ResponseDto<ResponseStatic>>
    getStaticData(@RequestBody @Valid RequestStatic requestStatic){

        return ResponseEntity.ok(
                new ResponseDto<>(ResponseStatus.SUCCESS.name(),"조회 성공",
                        adminDashBoardService.getStaticData(requestStatic))
        );
    }


    @Operation(summary = "최신 데이터 가져오기", description = "종류별 최신 데이터를 가져옵니다. 대시보드 상에서" +
            "알림목록에서 쓰는 최신 데이터랑" +
            "통계 데이터 보여주는 화면에서 나오는 최신데이터 모두 같은 api를 쓰시면될거같습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @PostMapping("/latest")
    public ResponseEntity<ResponseDto<List<ResponseLatestData>>>
    getLatestData(@RequestBody @Valid RequestLatestData requestLatestData){

        return ResponseEntity.ok(
                new ResponseDto<>(ResponseStatus.SUCCESS.name(),"조회 성공",
                        adminDashBoardService.getLatestData(requestLatestData)));

    }
}
