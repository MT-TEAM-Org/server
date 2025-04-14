package org.myteam.server.member.controller;

import static org.myteam.server.global.security.jwt.JwtProvider.*;
import static org.myteam.server.global.web.response.ResponseStatus.*;

import java.time.Duration;
import java.util.UUID;

import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.dto.FindIdResponse;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.dto.PasswordChangeRequest;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@Slf4j
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "내 정보 관리 API", description = "회원 정보 조회 및 수정 API")
public class MyInfoController {

	private final MemberReadService memberReadService;
	private final MemberService memberService;
	private final JwtProvider jwtProvider;

	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원가입 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "동일한 아이디 요", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/create")
	public ResponseEntity<?> create(@RequestBody @Valid MemberSaveRequest memberSaveRequest,
		BindingResult bindingResult,
		HttpServletResponse httpServletResponse
	) {
		log.info("MyInfoController create 메서드 실행");
		MemberResponse response = memberService.create(memberSaveRequest);

		// Authorization
		String accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1),
			response.getPublicId(), response.getRole().name(), response.getStatus().name());

		// 응답 헤더 설정
		httpServletResponse.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);
		return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원가입 성공", response), HttpStatus.CREATED);
	}

	@Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping
	public ResponseEntity<ResponseDto<MemberResponse>> get(@AuthenticationPrincipal CustomUserDetails userDetails) {
		log.info("MyInfoController get 메서드 실행");
		log.info("publicId : {}", userDetails.getPublicId());

		UUID publicId = userDetails.getPublicId();
		MemberResponse response = memberReadService.getByPublicId(publicId);
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"로그인 회원 정보 조회 성공",
			response
		));
	}

	@Operation(summary = "비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "본인이나 관리자 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeRequest passwordChangeRequest,
		BindingResult bindingResult,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		log.info("MyInfoController changePassword 메서드 실행 : {}", passwordChangeRequest.toString());
		String email = memberReadService.getCurrentLoginUserEmail(userDetails.getPublicId()); // 현재 로그인한 사용자 이메일
		memberService.changePassword(email, passwordChangeRequest);
		return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "비밀번호 변경 성공", null), HttpStatus.OK);
	}

	@Operation(summary = "아이디 찾기", description = "전화번호를 이용하여 사용자의 아이디를 찾습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "아이디 찾기 성공"),
		@ApiResponse(responseCode = "400", description = "휴대폰번호를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/find-id")
	public ResponseEntity<ResponseDto<FindIdResponse>> findUserEmailByTel(@RequestParam String phoneNumber) {
		FindIdResponse response = memberReadService.findUserId(phoneNumber);
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"아이디 찾기 성공",
			response
		));
	}

	@Operation(summary = "비밀번호 찾기", description = "이메일을 이용하여 임시 비밀번호를 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "임시 비밀번호 생성 성공"),
		@ApiResponse(responseCode = "401", description = "이메일 오류 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "본인이나 관리자 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "이메일 생성 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "이메일 전송 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/find-password")
	public ResponseEntity<?> resetPassword(@RequestParam String email) {
		memberService.generateTemporaryPassword(email);

		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"랜덤 비밀번호 생성 성공",
			null
		));
	}
}
