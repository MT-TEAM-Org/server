package org.myteam.server.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.auth.service.ReIssueService;
import org.myteam.server.global.security.dto.UserLoginEvent;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.repository.MemberActivityRepository;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.oauth2.dto.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

import static org.myteam.server.auth.controller.ReIssueController.LOGOUT_PATH;
import static org.myteam.server.auth.controller.ReIssueController.TOKEN_REISSUE_PATH;
import static org.myteam.server.global.security.jwt.JwtProvider.REFRESH_TOKEN_KEY;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_REFRESH;
import static org.myteam.server.global.util.cookie.CookieUtil.createCookie;
import static org.myteam.server.global.util.domain.DomainUtil.extractDomain;
import static org.myteam.server.member.domain.MemberStatus.*;

@Slf4j
@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${app.frontend.url}")
    private String frontUrl;
    private final String frontSignUpPath = "/sign"; // 프론트 회원가입 주소
    private final JwtProvider jwtProvider;
    private final MemberJpaRepository memberJpaRepository;
    private final ReIssueService reIssueService;
    private final ApplicationEventPublisher eventPublisher;

    public CustomOauth2SuccessHandler(JwtProvider jwtProvider,
                                      MemberJpaRepository memberJpaRepository,
                                      ReIssueService reIssueService,
                                      ApplicationEventPublisher eventPublisher) {
        this.jwtProvider = jwtProvider;
        this.memberJpaRepository = memberJpaRepository;
        this.reIssueService = reIssueService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess : Oauth 인증 성공");
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String email = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        String status = customUserDetails.getStatus().name();

        log.info("onAuthenticationSuccess email: {}", email);
        log.info("onAuthenticationSuccess role: {}", role);
        log.info("onAuthenticationSuccess frontUrl: {}", frontUrl);
        //유저확인
        Member member = memberJpaRepository.findByEmail(email).orElse(null);

        if (status.equals(PENDING.name())) {
            log.warn("PENDING 상태인 경우 로그인이 불가능합니다");

            // X-Refresh-Token
            String refreshToken = jwtProvider.generateToken(TOKEN_CATEGORY_REFRESH, Duration.ofDays(1), member.getPublicId(), member.getRole().name(), member.getStatus().name());

            reIssueService.deleteByPublicId(member.getPublicId());
            reIssueService.addRefreshEntity(member.getPublicId(), refreshToken, Duration.ofDays(1));

            log.warn("cookieValue refreshToken 확인용: {}", refreshToken);
            log.warn("cookieValue PublicId 확인용: {}", member.getPublicId());

            // 24 시간 유효한 리프레시 토큰을 생성
            response.addCookie(createCookie(REFRESH_TOKEN_KEY, refreshToken, TOKEN_REISSUE_PATH, 24 * 60 * 60, true, request.getServerName()));
            response.addCookie(createCookie(REFRESH_TOKEN_KEY, refreshToken, LOGOUT_PATH, 24 * 60 * 60, true, request.getServerName()));
//            String redirectUrl = String.format("%s?status=%s&email=%s", frontUrl, PENDING.name(), email);
            String redirectUrl = String.format("%s?refreshToken=%s", frontUrl, refreshToken);
            log.info("redirectUrl: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
            return;
        } else if (status.equals(INACTIVE.name())) {
            log.warn("INACTIVE 상태인 경우 로그인이 불가능합니다");
            // sendErrorResponse(response, HttpStatus.FORBIDDEN, "INACTIVE 상태인 경우 로그인이 불가능합니다");
            response.sendRedirect(frontUrl + "?status=" + INACTIVE.name());
            return;
        } else if (!status.equals(ACTIVE.name())) {
            log.warn("알 수 없는 유저 상태 코드 : " + status);
            // sendErrorResponse(response, HttpStatus.FORBIDDEN, "INACTIVE 상태인 경우 로그인이 불가능합니다");
            response.sendRedirect(frontUrl + "?status=" + ACTIVE.name());
            return;
        }

        log.info("onAuthenticationSuccess publicId: {}", member.getPublicId());
        log.info("onAuthenticationSuccess role: {}", member.getRole());

        // Authorization
        String refreshToken = jwtProvider.generateToken(TOKEN_CATEGORY_REFRESH, Duration.ofDays(1), member.getPublicId(), member.getRole().name(), member.getStatus().name());

        response.addCookie(createCookie(REFRESH_TOKEN_KEY, refreshToken, TOKEN_REISSUE_PATH, 24 * 60 * 60, true, extractDomain(request.getServerName())));
        response.addCookie(createCookie(REFRESH_TOKEN_KEY, refreshToken, LOGOUT_PATH, 24 * 60 * 60, true, extractDomain(request.getServerName())));

        log.debug("print refreshToken: {}", refreshToken);
        log.debug("print frontUrl: {}", frontUrl);

        eventPublisher.publishEvent(new UserLoginEvent(this, member.getPublicId()));

        response.sendRedirect(frontUrl);
        log.debug("Oauth 로그인에 성공하였습니다.");
    }
}
