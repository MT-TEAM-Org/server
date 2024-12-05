package org.myteam.server.oauth2.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import org.myteam.server.oauth2.service.OAuth2UserPrincipal;
import org.myteam.server.oauth2.user.OAuth2Provider;
import org.myteam.server.oauth2.user.OAuth2UserUnlinkManager;
import org.myteam.server.oauth2.util.CookieUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import static org.myteam.server.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.MODE_PARAM_COOKIE_NAME;
import static org.myteam.server.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2UserUnlinkManager oAuth2UserUnlinkManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl;

        targetUrl = determineTargetUrl(request, response, authentication);
        log.info("onAuthenticationSuccess: {}", targetUrl);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String mode = CookieUtils.getCookie(request, MODE_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("");

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);

        if (principal == null) {
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", "Login failed")
                    .build().toUriString();
        }

        if ("login".equalsIgnoreCase(mode)) {
            // TODO: DB 저장
            // TODO: 액세스 토큰, 리프레시 토큰 발급
            // TODO: 리프레시 토큰 DB 저장
            log.info("email={}, name={}, nickname={}, accessToken={}",
                    principal.getUserInfo().getEmail(),
                    principal.getUserInfo().getName(),
                    principal.getUserInfo().getNickname(),
                    principal.getUserInfo().getAccessToken()
            );

            String email = principal.getUserInfo().getEmail();
            String name = principal.getUserInfo().getName() == null ? (String) principal.getUserInfo().getAttributes().get("nickname") : principal.getUserInfo().getName();
            String password = passwordEncoder.encode(principal.getUserInfo().getId());


            Member member = null;
            if (!memberRepository.existsMemberByEmail(email)) {
                log.info("new Member");
                member = Member.builder()
                        .email(email)
                        .username(name)
                        .password(password)
                        .role("USER")
                        .build();

                memberRepository.save(member);
            } else {
                log.info("exists Member");
                member = memberRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Member not found"));
            }

            Duration duration = Duration.ofHours(1);

            String accessToken = jwtProvider.generateToken(duration, member.getPublicId(), member.getRole());
            String refreshToken = jwtProvider.generateToken(Duration.ofDays(7), member.getPublicId(), member.getRole());

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("access_token", accessToken)
                    .queryParam("refresh_token", refreshToken)
                    .build().toUriString();

        } else if ("unlink".equalsIgnoreCase(mode)) {

            String accessToken = principal.getUserInfo().getAccessToken();
            OAuth2Provider provider = principal.getUserInfo().getProvider();

            // TODO: DB 삭제
            // TODO: 리프레시 토큰 삭제
            oAuth2UserUnlinkManager.unlink(provider, accessToken);

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .build().toUriString();
        }

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", "Login failed")
                .build().toUriString();
    }

    private OAuth2UserPrincipal getOAuth2UserPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2UserPrincipal) {
            return (OAuth2UserPrincipal) principal;
        }
        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
