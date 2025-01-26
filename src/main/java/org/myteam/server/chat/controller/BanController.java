package org.myteam.server.chat.controller;

import lombok.RequiredArgsConstructor;
import org.myteam.server.chat.dto.request.BanRequest;
import org.myteam.server.chat.dto.response.BanResponse;
import org.myteam.server.chat.service.BanService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

/**
 * Ban 도메인에 대한 HTTP 요청 처리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bans")
public class BanController {

    private final BanService banService;

    /**
     * 유저 밴하기
     */
    @PostMapping
    public ResponseEntity<ResponseDto<BanResponse>> banUser(@RequestBody BanRequest request) {
        BanResponse response = banService.banUser(request);
        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "Ban Success",
                response
        ));
    }

    /**
     * 유저 밴 해제
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<ResponseDto<String>> unbanUser(@PathVariable String username) {
        String deleteName = banService.unbanUser(username);
        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "Delete Ban Successfully",
                deleteName
        ));
    }

    /**
     * 특정 유저 밴 정보 조회
     */
    @GetMapping("/{username}")
    public ResponseEntity<ResponseDto<BanResponse>> getBanByUsername(@PathVariable String username) {
        BanResponse response = banService.findBanByUsername(username);
        return ResponseEntity.ok(new ResponseDto(
                SUCCESS.name(),
                "Find Ban Reason Successfully",
                response
        ));
    }
}
