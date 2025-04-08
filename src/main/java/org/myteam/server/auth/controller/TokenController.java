package org.myteam.server.auth.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.auth.service.TokenService;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO_ : 리프레시 토큰에 대한 블랙 리스트 작성
 */
@Slf4j
@RestController
@RequestMapping("/api/token")
@Tag(name = "토큰 재발급 API", description = "Access Token 및 Refresh Token 재발급 API")
public class TokenController {
	private final TokenService tokenService;

	public TokenController(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Operation(summary = "토큰 재발급", description = "만료된 Access Token을 Refresh Token을 이용하여 재발급합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
		@ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/regenerate")
	public ResponseEntity<ResponseDto<?>> regenerate(HttpServletRequest request, HttpServletResponse response) {
		tokenService.regenerateAccessToken(request, response);

		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"AccessToken 재발급 성공",
			null));
	}
}
