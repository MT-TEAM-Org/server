package org.myteam.server.home.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.home.dto.HotBoardDto;
import org.myteam.server.home.dto.NewBoardDto;
import org.myteam.server.home.dto.TotalListResponse;
import org.myteam.server.home.dto.TotalSearchRequest;
import org.myteam.server.home.service.TotalReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Tag(name = "메인 홈 API", description = "메인 홈 화면 관련 API")
public class HomeController {

    private final TotalReadService totalReadService;

    /**
     * 실시간 HOT 게시글 목록 조회
     */
    @Operation(summary = "실시간 HOT 게시글 목록 조회", description = "실시간 HOT 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "실시간 HOT 게시글 목록 조회 성공")
    })
    @GetMapping("/hot")
    public ResponseEntity<ResponseDto<List<HotBoardDto>>> getHomeHotList() {
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "실시간 HOT 게시글 목록 조회 성공",
                totalReadService.getHotBoardList()));
    }

    /**
     * 실시간 최신 게시글 목록 조회
     */
    @Operation(summary = "실시간 최신 게시글 목록 조회", description = "실시간 최신 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "실시간 최신 게시글 목록 조회 성공")
    })
    @GetMapping("/new")
    public ResponseEntity<ResponseDto<List<NewBoardDto>>> getHomeNewList() {
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "실시간 최신 게시글 목록 조회 성공",
                totalReadService.getNewBoardList()));
    }

    /**
     * 통합 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseDto<TotalListResponse<?>>> getTotalSearch(
            @Valid @ModelAttribute TotalSearchRequest request) {

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "통합 검색 조회 성공",
                totalReadService.getTotalList(request.toServiceRequest())
        ));
    }
}