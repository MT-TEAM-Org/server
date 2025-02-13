package org.myteam.server.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.auth.service.ReIssueService;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.dto.PasswordChangeRequest;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

import static org.myteam.server.global.security.jwt.JwtProvider.*;
import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MyInfoController {

    private final MemberReadService memberReadService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final ReIssueService reIssueService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid MemberSaveRequest memberSaveRequest,
                                    BindingResult bindingResult,
                                    HttpServletResponse httpServletResponse
    ) {
        log.info("MyInfoController create 메서드 실행");
        MemberResponse response = memberService.create(memberSaveRequest);

        // Authorization
        String accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1), response.getPublicId(), response.getRole().name(), response.getStatus().name());

        // 응답 헤더 설정
        httpServletResponse.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원가입 성공", response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> get(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("MyInfoController get 메서드 실행");
        log.info("publicId : {}", userDetails.getPublicId());

        UUID publicId = userDetails.getPublicId();
        MemberResponse response = memberReadService.getByPublicId(publicId);
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "로그인 회원 정보 조회 성공", response), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeRequest passwordChangeRequest,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("MyInfoController changePassword 메서드 실행 : {}", passwordChangeRequest.toString());
        String email = memberReadService.getCurrentLoginUserEmail(userDetails.getPublicId()); // 현재 로그인한 사용자 이메일
        memberService.changePassword(email, passwordChangeRequest);
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "비밀번호 변경 성공", null), HttpStatus.OK);
    }

    @PostMapping("/find-id")
    public ResponseEntity<ResponseDto<String>> findUserId(@RequestParam String phoneNumber) {
        String userEmail = memberReadService.findUserId(phoneNumber);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "아이디 찾기 성공",
                userEmail
        ));
    }
}
