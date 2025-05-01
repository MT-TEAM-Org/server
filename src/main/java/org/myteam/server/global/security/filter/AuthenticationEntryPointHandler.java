package org.myteam.server.global.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    /*
     * 인증 되지 않은사용자가 인증 페이지나 API를 요청을 할 때 발생한다
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        logger.info("get in AuthenticationEntryPointHandler");
        logger.error("Authentication error: {}, url: {}", authException.toString(), request.getRequestURI());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"UNAUTHORIZED\", \"message\": \"Invalid or expired token.\"}");
    }
}
