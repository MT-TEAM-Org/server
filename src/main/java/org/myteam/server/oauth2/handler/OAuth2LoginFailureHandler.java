package org.myteam.server.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ExistingUserAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // interaction_required 발생했는지 검사
        String errorMessage = request.getParameter("error");

        if ("interaction_required".equals(errorMessage)) {
            // 다시 prompt=consent로 리다이렉트
            getRedirectStrategy().sendRedirect(request, response, "/oauth2/authorization/google?prompt=consent");
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
