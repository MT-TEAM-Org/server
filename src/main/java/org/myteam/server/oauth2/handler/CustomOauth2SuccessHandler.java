package org.myteam.server.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.oauth2.dto.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

import static org.myteam.server.auth.controller.ReIssueController.LOGOUT_PATH;
import static org.myteam.server.auth.controller.ReIssueController.TOKEN_REISSUE_PATH;
import static org.myteam.server.global.security.jwt.JwtProvider.*;
import static org.myteam.server.member.domain.MemberStatus.*;
import static org.myteam.server.util.cookie.CookieUtil.createCookie;

@Slf4j
@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${FRONT_URL:http://localhost:3000}")
    private String frontUrl;
    private static final String ACCESS_TOKEN_KEY = "Authorization";
    private static final String REFRESH_TOKEN_KEY = "X-Refresh-Token";
    private final JwtProvider jwtProvider;
    private final MemberJpaRepository memberJpaRepository;

    public CustomOauth2SuccessHandler(JwtProvider jwtProvider, MemberJpaRepository memberJpaRepository) {
        this.jwtProvider = jwtProvider;
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess : Oauth 인증 성공");
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String email = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        String status = customUserDetails.getStatus().name();

        if (status.equals(PENDING.name())) {
            log.warn("PENDING 상태인 경우 로그인이 불가능합니다");
            // sendErrorResponse(response, HttpStatus.FORBIDDEN, "PENDING 상태인 경우 로그인이 불가능합니다");
            response.sendRedirect(frontUrl + "?status=" + status);
            return;
        } else if (status.equals(INACTIVE.name())) {
            log.warn("INACTIVE 상태인 경우 로그인이 불가능합니다");
            // sendErrorResponse(response, HttpStatus.FORBIDDEN, "INACTIVE 상태인 경우 로그인이 불가능합니다");
            response.sendRedirect(frontUrl + "?status=" + status);
            return;
        } else if (!status.equals(ACTIVE.name())) {
            log.warn("알 수 없는 유저 상태 코드 : " + status);
            // sendErrorResponse(response, HttpStatus.FORBIDDEN, "INACTIVE 상태인 경우 로그인이 불가능합니다");
            response.sendRedirect(frontUrl + "?status=" + status);
            return;
        }

        log.info("onAuthenticationSuccess email: {}", email);
        log.info("onAuthenticationSuccess role: {}", role);
        //유저확인
        Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        log.info("onAuthenticationSuccess publicId: {}", member.getPublicId());
        log.info("onAuthenticationSuccess role: {}", member.getRole());
        // Authorization
        String accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofHours(1), member.getPublicId(), member.getRole().name(), member.getStatus().name());
        // X-Refresh-Token
        String refreshToken = jwtProvider.generateToken(TOKEN_CATEGORY_REFRESH, Duration.ofDays(7), member.getPublicId(), member.getRole().name(), member.getStatus().name());
        String cookieValue = URLEncoder.encode("Bearer " + refreshToken, StandardCharsets.UTF_8);

        // redirect 순간 Header 값 날아감
        // response.addHeader(ACCESS_TOKEN_KEY, "Bearer " + accessToken);
        response.addCookie(createCookie(REFRESH_TOKEN_KEY, cookieValue, TOKEN_REISSUE_PATH, 24 * 60 * 60, true));
        response.addCookie(createCookie(REFRESH_TOKEN_KEY, cookieValue, LOGOUT_PATH, 24 * 60 * 60, true));

        log.debug("print accessToken: {}", accessToken);
        log.debug("print refreshToken: {}", refreshToken);
        log.debug("print frontUrl: {}", frontUrl);

//        frontUrl += "?" + ACCESS_TOKEN_KEY + "=" + ("Bearer%20" + accessToken);
//        frontUrl += "&" + REFRESH_TOKEN_KEY + "=" + ("Bearer%20" + refreshToken);
        // front 로 리다이렉트 후 Access Token 재갱신 처리하도록 권유
        response.sendRedirect(frontUrl);

        log.debug("Oauth 로그인에 성공하였습니다.");
    }
}
