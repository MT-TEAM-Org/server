package org.myteam.server.profile.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.profile.dto.response.ProfileResponseDto.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class MemberProfileController {

    private final MemberReadService memberReadService;

    @GetMapping
    public ResponseEntity<ResponseDto<ProfileResponse>> getMemberProfile() {
        ProfileResponse response = memberReadService.getProfile();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "성공적으로 조회되었습니다.",
                response
        ));
    }
}
