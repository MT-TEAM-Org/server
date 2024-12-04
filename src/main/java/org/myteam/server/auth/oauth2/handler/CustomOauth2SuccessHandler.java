package org.myteam.server.auth.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.auth.oauth2.dto.CustomOAuth2User;
import org.myteam.server.global.jwt.JwtProvider;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final String ACCESS_TOKEN_KEY = "Authorization";
    public static final String REFRESH_TOKEN_KEY = "X-Refresh-Token";
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public CustomOauth2SuccessHandler(JwtProvider jwtProvider, MemberRepository memberRepository) {
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess : Oauth 로그인 성공");
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        log.debug("onAuthenticationSuccess username: {}", username);
        log.debug("onAuthenticationSuccess role: {}", role);

        //유저확인
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        log.debug("onAuthenticationSuccess publicId: {}", member.getPublicId());
        log.debug("onAuthenticationSuccess role: {}", member.getRole());

        // Authorization
        String accessToken = jwtProvider.generateToken(Duration.ofHours(1), member.getPublicId(), member.getRole());
        // X-Refresh-Token
        String refreshToken = jwtProvider.generateToken(Duration.ofDays(7), member.getPublicId(), member.getRole());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(ACCESS_TOKEN_KEY, accessToken);
        response.setHeader(REFRESH_TOKEN_KEY, refreshToken);
        response.getWriter().write("accessToken = " + accessToken);
        response.getWriter().write("\n");
        response.getWriter().write("refreshToken = " + refreshToken);
        response.getWriter().write("\n");
        response.getWriter().write("boolean = " + jwtProvider.validToken(accessToken));
        response.getWriter().write("\n");
        log.debug("Oauth 로그인에 성공하였습니다.");
    }
}
