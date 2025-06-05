package org.myteam.server.global.security.handler;

import static org.myteam.server.global.security.jwt.JwtProvider.HEADER_AUTHORIZATION;
import static org.springframework.http.HttpMethod.*;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.util.redis.service.RedisUserInfoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	private final JwtProvider jwtProvider;
	private final RedisUserInfoService redisUserInfoService;

	public CustomLogoutSuccessHandler(JwtProvider jwtProvider,
									  RedisUserInfoService redisUserInfoService) {
		this.jwtProvider = jwtProvider;
		this.redisUserInfoService = redisUserInfoService;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
								HttpServletResponse response,
								Authentication authentication) {
		log.info("LogoutSuccessHandler onLogoutSuccess() 메서드를 실행하였습니다");

		String method = request.getMethod();
		if (!method.equals(POST.name())) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
		String accessToken = jwtProvider.getAccessToken(authorizationHeader);
		redisUserInfoService.deleteUserInfo(accessToken);
		log.info("delete user info: {}", accessToken);

		// 1. Security Context 해제
		clearContextAndAddCookie(response);
	}

	/**
	 * SecurityContext 초기화
	 *
	 * @param response HttpServletResponse
	 */
	private void clearContextAndAddCookie(HttpServletResponse response) {
		SecurityContextHolder.clearContext();
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
