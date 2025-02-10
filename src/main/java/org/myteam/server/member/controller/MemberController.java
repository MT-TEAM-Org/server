package org.myteam.server.member.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.ExistMemberNicknameRequest;
import org.myteam.server.member.dto.MemberRoleUpdateRequest;
import org.myteam.server.member.dto.MemberStatusUpdateRequest;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static org.myteam.server.global.security.jwt.JwtProvider.*;
import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberReadService memberReadService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    /**
     * 이메일로 사용자 존재 여부 확인
     */
    @Deprecated
    @GetMapping("/exists/email")
    public ResponseEntity<?> existsByEmail(@Valid ExistMemberNicknameRequest existMemberNicknameRequest, BindingResult bindingResult) {
//        log.info("MemberController existsByEmail 메서드 실행 : {}", existMemberNicknameRequest.getEmail());
//        boolean exists = memberService.existsByEmail(existMemberNicknameRequest.getEmail());
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "이메일 존재 여부 확인", null));
    }

    /**
     * 닉네임으로 사용자 존재 여부 확인
     */
    @GetMapping("/exists/nickname")
    public ResponseEntity<?> existsByNickname(@Valid ExistMemberNicknameRequest existMemberNicknameRequest, BindingResult bindingResult) {
        log.info("MemberController existsByNickname 메서드 실행 : {}", existMemberNicknameRequest.getNickname());
        boolean exists = memberReadService.existsByNickname(existMemberNicknameRequest.getNickname());
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "닉네임 존재 여부 확인", exists));
    }

    /**
     * 이메일을 통해 사용자의 가입 유형을 확인합니다.
     *
     * @param email 사용자의 이메일
     * @return null: 가입되지 않은 사용자, "LOCAL": 로컬 회원가입 사용자, 그 외: 소셜 로그인 사용자
     */
    @GetMapping("/{email}/type")
    public ResponseEntity<?> getMemberType(@PathVariable String email) {
        log.info("MemberController getMemberType 메서드 실행: {}", email);
        MemberType memberType = memberReadService.getMemberTypeByEmail(email);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "회원 타입 조회 성공", memberType));
    }

    // TODO_ : 헤더에 publicId 를 넣어달라고 하고 받아서... 끄내고 아이디 조회해서 그걸로 다시 own 검토하면 될 것으로 보임
    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody @Valid MemberStatusUpdateRequest memberStatusUpdateRequest,
                                          BindingResult bindingResult,
                                          HttpServletRequest httpServletRequest) {
        log.info("MyInfoController updateStatus 메서드 실행");
        String authorizationHeader = httpServletRequest.getHeader(HEADER_AUTHORIZATION);

        // accessToken 으로 부터 유저 정보 반환
        MemberResponse response = memberReadService.getAuthenticatedMember(authorizationHeader);

        log.info("email : {}", response.getEmail());

        // 서비스 호출
        String targetEmail = response.getEmail(); // 변경을 시도하는 유저의 이메일 (본인 또는 관리자)

        memberService.updateStatus(targetEmail, memberStatusUpdateRequest);

        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "회원 상태가 성공적으로 변경되었습니다.", null));
    }

    @PutMapping("/role")
    public ResponseEntity<?> updateRole(@RequestBody @Valid MemberRoleUpdateRequest memberRoleUpdateRequest) {
        log.info("MemberController updateRole 메서드 실행. memberTypeUpdateRequest : {}", memberRoleUpdateRequest);
        MemberResponse response = memberService.updateRole(memberRoleUpdateRequest);
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "권한 변경 성공", response), HttpStatus.OK);
    }

    @Deprecated
    @GetMapping("/get-token/user/{email}/second/{second}")
    public ResponseEntity<?> getToken(@PathVariable String email, @PathVariable Integer second) {
        log.info("getToken 메서드가 실행되었습니다.");
        MemberResponse response = memberReadService.getByEmail(email);
        String encode = TOKEN_PREFIX + jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofSeconds(second), response.getPublicId(), response.getRole().name(), response.getStatus().name());
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "토큰 조회 성공", encode), HttpStatus.OK);
    }
}
