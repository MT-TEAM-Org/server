package org.myteam.server.oauth2.handler;

import static org.myteam.server.global.security.jwt.JwtProvider.*;
import static org.myteam.server.member.domain.MemberStatus.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

import org.myteam.server.global.security.dto.UserLoginEvent;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.util.redis.RedisService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.oauth2.dto.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Value("${app.frontend.url.dev}")
	private String frontUrl;
	private final String frontSignUpPath = "/sign"; // 프론트 회원가입 주소
	private final JwtProvider jwtProvider;
	private final MemberJpaRepository memberJpaRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final RedisService redisService;

	public CustomOauth2SuccessHandler(JwtProvider jwtProvider,
		MemberJpaRepository memberJpaRepository,
		ApplicationEventPublisher eventPublisher,
		RedisService redisService) {
		this.jwtProvider = jwtProvider;
		this.memberJpaRepository = memberJpaRepository;
		this.eventPublisher = eventPublisher;
		this.redisService = redisService;
	}

	@Override
	@Transactional
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		log.info("onAuthenticationSuccess : Oauth 인증 성공");
		CustomOAuth2User customUserDetails = (CustomOAuth2User)authentication.getPrincipal();

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
		Member member = memberJpaRepository.findByEmailAndType(email, customUserDetails.getType()).orElse(null);

		if (status.equals(PENDING.name())) {
			log.warn("PENDING 상태인 경우 로그인이 불가능합니다");
			log.warn("cookieValue PublicId 확인용: {}", member.getPublicId());

			// Authorization
			String accessToken = generateAccessToken(member, role, status);
			String refreshToken = generateRefreshToken(member, role, status);

			redisService.putRefreshToken(member.getPublicId(), refreshToken);

			log.debug("print accessToken: {}", accessToken);
			log.debug("print role: {}", role);

			response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);

			eventPublisher.publishEvent(new UserLoginEvent(this, member.getPublicId()));

			String redirectUrl = frontUrl + "/sign?sign=signup&?refreshToken=" + URLEncoder.encode(refreshToken,
				StandardCharsets.UTF_8);
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

		log.debug("print frontUrl: {}", frontUrl);

		// Authorization
		String accessToken = generateAccessToken(member, role, status);
		String refreshToken = generateRefreshToken(member, role, status);

		log.debug("print accessToken: {}", accessToken);
		log.debug("print role: {}", role);

		redisService.putRefreshToken(member.getPublicId(), refreshToken);
		response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);

		eventPublisher.publishEvent(new UserLoginEvent(this, member.getPublicId()));

		response.sendRedirect(frontUrl);
		log.debug("Oauth 로그인에 성공하였습니다.");
	}

	private String generateAccessToken(Member member, String role, String status) {
		return jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1), member.getPublicId(), role, status);
	}

	private String generateRefreshToken(Member member, String role, String status) {
		return jwtProvider.generateToken(TOKEN_CATEGORY_REFRESH, Duration.ofDays(30), member.getPublicId(), role,
			status);
	}
}
