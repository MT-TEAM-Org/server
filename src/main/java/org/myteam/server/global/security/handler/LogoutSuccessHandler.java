package org.myteam.server.global.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myteam.server.auth.repository.RefreshJpaRepository;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.myteam.server.global.security.jwt.JwtProvider.REFRESH_TOKEN_KEY;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_REFRESH;
import static org.springframework.http.HttpMethod.POST;

public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    final JwtProvider jwtProvider;
    final RefreshJpaRepository refreshJpaRepository;

    public LogoutSuccessHandler(JwtProvider jwtProvider, RefreshJpaRepository refreshJpaRepository) {
        this.jwtProvider = jwtProvider;
        this.refreshJpaRepository = refreshJpaRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        logger.info("LogoutSuccessHandler onLogoutSuccess() 메서드를 실행하였습니다");

        String method = request.getMethod();
        if (!method.equals(POST.name())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        // get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(REFRESH_TOKEN_KEY)) {
                    refresh = cookie.getValue();
                }
            }
        }

        if (refresh != null) {
            try {
                refresh = jwtProvider.getAccessToken(URLDecoder.decode(refresh, StandardCharsets.UTF_8));

                // expired check
                logger.debug("===========================");
                logger.debug(refresh);
                logger.debug("===========================");

                jwtProvider.isExpired(refresh);

                // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
                String category = jwtProvider.getCategory(refresh);
                if (!category.equals(TOKEN_CATEGORY_REFRESH)) {
                    logger.warn("잘못된 JWT 토큰 형식");
                    clearContextAndAddCookie(response);
                    return;
                }

                UUID publicId = jwtProvider.getPublicId(refresh);

                // DB에 저장되어 있는지 확인
                Boolean isExist = refreshJpaRepository.existsByRefreshAndPublicId(refresh, publicId);
                if (!isExist) {
                    logger.warn("인증되지 않은 토큰");
                    clearContextAndAddCookie(response);
                    return;
                }

                // 로그아웃 진행
                // Refresh 토큰 DB 에서 제거
                refreshJpaRepository.deleteByPublicId(publicId);
            } catch (Exception e) {
                logger.error("리프레시 토큰 처리 중 예외 발생", e);
            }
        }

        // 1. Security Context 해제 및 빈 쿠키 추가
        clearContextAndAddCookie(response);
    }

    /**
     * SecurityContext 초기화 및 빈 쿠키 추가 메서드
     *
     * @param response HttpServletResponse
     */
    private void clearContextAndAddCookie(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
