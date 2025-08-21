package org.myteam.server.chat.block.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.myteam.server.chat.block.dto.request.BlockRequest.*;
import org.myteam.server.chat.block.dto.response.BlockResponse.*;
import org.myteam.server.chat.block.dto.response.BlockedMembersResponse;
import org.myteam.server.chat.block.service.BlockReadService;
import org.myteam.server.chat.block.service.BlockService;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.simpleframework.xml.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

/**
 * Ban 도메인에 대한 HTTP 요청 처리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/block")
@Tag(name = "채팅 차단 관련 api", description = "채팅 차단 추가,삭제 및 차단 목록 조회")
public class BlockController {

    private final BlockService blockService;
    private final BlockReadService blockReadService;

    /**
     * 유저 밴하기
     */
    @Operation(summary = "차단 추가", description = "특정 이용자의 차단 리스트에 추가합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원, 게시글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/add/{blockedId}")
    public ResponseEntity<ResponseDto<SuccessBlockResponse>> banUser(@PathVariable(value = "blockedId",required = true) UUID blockedId){
        SuccessBlockResponse response = blockService.banUser(blockedId);
        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "Ban Success",
                response
        ));
    }
    /**
     * 유저 밴 해제
     */
    @Operation(summary = "차단 해제", description = "특정 이용자의 차단 리스트에 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/del/{blockedId}")
    public ResponseEntity<ResponseDto<String>> unbanUser(@PathVariable(required = true) UUID blockedId) {
        blockService.unblockUser(blockedId);
        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "Delete Ban Successfully",
                null
        ));
    }

    /**
     * 특정 유저 밴 정보 조회
     */
    @Operation(summary = "차단 목록조회", description = "특정 이용자의 차단 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{blockerId}/blocked")
    public ResponseEntity<ResponseDto<BlockedMembersResponse>> getBanByPublicId(@PathVariable UUID blockerId) {
        BlockedMembersResponse response = blockReadService.getBlockedUsers(blockerId);
        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "Find Ban Reason Successfully",
                response
        ));
    }
}
