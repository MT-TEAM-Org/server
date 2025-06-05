package org.myteam.server.chat.block.controller;

import lombok.RequiredArgsConstructor;
import org.myteam.server.chat.block.dto.request.BlockRequest.*;
import org.myteam.server.chat.block.dto.response.BlockResponse.*;
import org.myteam.server.chat.block.dto.response.BlockedMembersResponse;
import org.myteam.server.chat.block.service.BlockReadService;
import org.myteam.server.chat.block.service.BlockService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

/**
 * Ban 도메인에 대한 HTTP 요청 처리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bans")
public class BlockController {

    private final BlockService blockService;
    private final BlockReadService blockReadService;

    /**
     * 유저 밴하기
     */
    @PostMapping
    public ResponseEntity<ResponseDto<SuccessBlockResponse>> banUser(@RequestBody BlockUserRequest request) {
        SuccessBlockResponse response = blockService.banUser(request);
        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "Ban Success",
                response
        ));
    }

    /**
     * 유저 밴 해제
     */
    @DeleteMapping("/{blockedId}")
    public ResponseEntity<ResponseDto<String>> unbanUser(@PathVariable UUID blockedId) {
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
