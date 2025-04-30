package org.myteam.server.game.controller;


import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.game.dto.response.GameEventListResponse;
import org.myteam.server.game.service.GameReadService;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Tag(name = "게임 할인 및 이벤트 관련 API", description = "게임 할인 및 이벤트 정보 API")
public class GameController {

    private final GameReadService gameReadService;

    /**
     * 게임 이벤트 목록 조회 API
     */
    @Operation(summary = "게임 이벤트 목록 조회", description = "진행중인 게임 이벤트 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게임 이벤트 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/event")
    public ResponseEntity<ResponseDto<GameEventListResponse>> getGameEventList(
            @ModelAttribute @Valid PageInfoRequest pageInfoRequest) {
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "게임 이벤트 목록 조회 성공",
                gameReadService.getGameEventList(pageInfoRequest.toServiceRequest())));
    }
}