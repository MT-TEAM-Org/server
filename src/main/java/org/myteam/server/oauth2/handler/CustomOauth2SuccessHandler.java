package org.myteam.server.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.oauth2.dto.CustomOAuth2User;
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
        String email = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        log.info("onAuthenticationSuccess email: {}", email);
        log.info("onAuthenticationSuccess role: {}", role);
        //유저확인
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        log.info("onAuthenticationSuccess publicId: {}", member.getPublicId());
        log.info("onAuthenticationSuccess role: {}", member.getRole());
        // Authorization
        String accessToken = jwtProvider.generateToken(Duration.ofHours(1), member.getPublicId(), member.getRole().toString());
        // X-Refresh-Token
        String refreshToken = jwtProvider.generateToken(Duration.ofDays(7), member.getPublicId(), member.getRole().toString());

        response.addHeader(ACCESS_TOKEN_KEY, "Bearer " + accessToken);
        response.addHeader(REFRESH_TOKEN_KEY, "Bearer " + refreshToken);
        log.debug("Oauth 로그인에 성공하였습니다.");
    }
}
