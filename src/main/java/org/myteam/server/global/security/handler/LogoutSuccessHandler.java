package org.myteam.server.global.security.handler;

import static org.springframework.http.HttpMethod.*;

import org.myteam.server.global.security.jwt.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogoutSuccessHandler
	implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
	final JwtProvider jwtProvider;

	public LogoutSuccessHandler(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		log.info("LogoutSuccessHandler onLogoutSuccess() 메서드를 실행하였습니다");

		String method = request.getMethod();
		if (!method.equals(POST.name())) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

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
