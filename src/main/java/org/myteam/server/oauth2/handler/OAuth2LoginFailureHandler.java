package org.myteam.server.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ExistingUserAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("소셜 로그인 실패! 서버 로그를 확인해주세요.");

        response.setContentType("application/json; charset=UTF-8"); // Content-Type 에 UTF-8 설정 추가
        response.setCharacterEncoding("UTF-8"); // 응답 인코딩을 UTF-8로 설정
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        if (exception instanceof ExistingUserAuthenticationException) {
            response.getWriter().write("{\"error\": \"USER_ALREADY_EXISTS\", \"message\": \"" + exception.getMessage() + "\"}");
        } else {
            response.getWriter().write("{\"error\": \"OAUTH2_UNAUTHORIZED\", \"message\": \"" + exception.getMessage() + "\"}");
        }

        log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception.getMessage());
    }
}
