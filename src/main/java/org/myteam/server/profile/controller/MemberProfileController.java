package org.myteam.server.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberWriteService;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberUpdateRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberDeleteRequest;
import org.myteam.server.profile.dto.response.ProfileResponseDto.ProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class MemberProfileController {

    private final MemberReadService memberReadService;
    private final MemberWriteService memberWriteService;

    @GetMapping
    public ResponseEntity<ResponseDto<ProfileResponse>> getMemberProfile() {
        ProfileResponse response = memberReadService.getProfile();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "성공적으로 조회되었습니다.",
                response
        ));
    }

    @PutMapping
    public ResponseEntity<ResponseDto<MemberResponse>> update(@RequestBody @Valid MemberUpdateRequest memberUpdateRequest) {
        log.info("회원 정보 수정 요청: {}", memberUpdateRequest.getEmail());

        MemberResponse response = memberWriteService.updateMemberProfile(memberUpdateRequest);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원정보 수정 성공",
                response
        ));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<String>> delete(@RequestBody @Valid MemberDeleteRequest memberDeleteRequest) {
        log.info("회원 삭제 요청");

        memberWriteService.deleteMember(memberDeleteRequest);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원 탈퇴 성공",
                null
        ));
    }

}
