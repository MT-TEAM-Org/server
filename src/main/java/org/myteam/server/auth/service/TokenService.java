package org.myteam.server.auth.service;

import static org.myteam.server.global.security.jwt.JwtProvider.*;

import java.time.Duration;
import java.util.UUID;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveJwtException;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.util.redis.service.RedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {
	private final JwtProvider jwtProvider;
	private final RedisService redisService;

	/**
	 * Refresh Token 검증
	 */
	public void regenerateAccessToken(HttpServletRequest request, HttpServletResponse response) {
		// 리프레시 토큰 만료 여부 체크
		String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
		String accessToken = jwtProvider.getAccessToken(authorizationHeader);

		UUID publicId = jwtProvider.getPublicIdWithoutExpired(accessToken);

		String refreshToken = redisService.getRefreshToken(publicId);
		// 리프레시 토큰 검증
		existRefreshToken(refreshToken);
		expireRefreshToken(refreshToken);

		generateAccessToken(refreshToken, response);
	}

	private void existRefreshToken(String refreshToken) {
		if (refreshToken == null) {
			throw new PlayHiveJwtException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	private void expireRefreshToken(String refreshToken) {
		Boolean expired = jwtProvider.isExpired(refreshToken);
		if (expired) {
			log.warn("토큰이 만료되었습니다.");
			throw new PlayHiveJwtException(ErrorCode.EXPIRED_REFRESH_TOKEN);
		}
	}

	private void generateAccessToken(String refreshToken, HttpServletResponse response) {
		UUID publicId = jwtProvider.getPublicId(refreshToken);
		String role = jwtProvider.getRole(refreshToken);
		String status = jwtProvider.getStatus(refreshToken);

		String newAccessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1), publicId, role,
			status);

		response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + newAccessToken);
	}
}
