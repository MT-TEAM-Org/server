package org.myteam.server.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.auth.dto.AuthRequest;
import org.myteam.server.auth.dto.AuthResponse;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static org.myteam.server.global.security.jwt.JwtProvider.*;
import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입 및 로그인 관련 API")
public class AuthTestController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MemberJpaRepository memberRepository;
    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "사용자가 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "아이디 중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto<MemberResponse>> create(@RequestBody @Valid MemberSaveRequest memberSaveRequest,
                                                              HttpServletResponse httpServletResponse) {
        log.info("MyInfoController create 메서드 실행");
        MemberResponse response = memberService.create(memberSaveRequest);

        // Authorization
        String accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1), response.getPublicId(), response.getRole().name(), response.getStatus().name());

        // 응답 헤더 설정
        httpServletResponse.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원가입 성공",
                response
        ));
    }

    @Operation(summary = "로그인", description = "사용자가 로그인하여 액세스 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "JSON 파싱 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<AuthResponse>> login(@RequestBody AuthRequest request) {
        log.info("🔐 로그인 요청 - email: {}", request.getEmail());

        // 1. 사용자 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. 사용자 정보 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 3. JWT 토큰 생성
        String accessToken1d = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1), member.getPublicId(), member.getRole().name(), member.getStatus().name());
        String accessToken30s = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofSeconds(30), member.getPublicId(), member.getRole().name(), member.getStatus().name());

        log.info("✅ 로그인 성공 - email: {}, accessToken1d: {}, accessToken30s: {}", member.getEmail(), accessToken1d, accessToken30s);

        // 4. 응답 반환
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "로그인 성공",
                new AuthResponse(accessToken1d, accessToken30s)
        ));
    }
}