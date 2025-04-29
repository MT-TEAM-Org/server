package org.myteam.server.global.security.config;

import static org.myteam.server.global.security.jwt.JwtProvider.*;

import org.myteam.server.global.security.filter.AuthenticationEntryPointHandler;
import org.myteam.server.global.security.filter.CustomAccessDeniedHandler;
import org.myteam.server.global.security.filter.JwtAuthenticationFilter;
import org.myteam.server.global.security.filter.TokenAuthenticationFilter;
import org.myteam.server.global.security.handler.LogoutSuccessHandler;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.security.service.CustomUserDetailsService;
import org.myteam.server.global.util.redis.RedisService;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.oauth2.handler.CustomOauth2SuccessHandler;
import org.myteam.server.oauth2.handler.OAuth2LoginFailureHandler;
import org.myteam.server.oauth2.resolver.CustomAuthorizationRequestResolver;
import org.myteam.server.oauth2.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.HstsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	/* 권한 제외 대상 */
	private static final String[] PERMIT_ALL_URLS = new String[] {
		// Test Endpoints
		/** @brief Exception Test */"/test/exception-test",
		/** @brief Can Access All */"/test/all/**",
		/** @brief Test login, create */"/api/test/**",
		/** @brief Test Slack Integration */"/test/slack",
		"/api/members/get-token/**", "/api/attachments/**", "/api/posts/**",
		// Chat
		"/ws-stomp/**",
		// Health Check
		/** @brief health check */
		"/status",
		// Swagger Documents
		/** @brief Swagger Docs */
		"/v3/api-docs/**", "/swagger-ui/**",
		// Database console
		/** @brief database url */
		"/h2-console",
		// Business Logic
		/** @brief about login */"/auth/**",
		/** @brief Allow static resource access */
		"/upload/**",
		/** @brief Allow user permission to change */
		"/api/members/role",
		"/api/certification/send",
		"/api/certification/certify-code",
		"/api/oauth2/members/email/**",
		"/api/members/type/**",
		"/api/me/create",
		"/api/token/regenerate",

		// 문의하기
		"/api/inquiry",

		// 아이디-비밀번호 찾기
		"/api/me/find-id/**",
		"api/me/find-password",

		"/api/notice-comments/**"
	};

	/**
	 * @brief Get 요청에서의 모든 사용자 인가
	 */
	private static final String[] PUBLIC_GET_URLS = {
		/** @brief 게시판 관련 URL */
		"/api/board/{boardId}", // 게시글 상세 조회
		"/api/board", // 게시글 목록 조회
		"/api/board/comment/{boardCommentId}", // 게시판 댓글 상세 조회
		"/api/board/{boardId}/comment",

		/** @brief 홈 메인*/
		"api/comments/{contentId}",
		"api/comments/{commentId}/detail",
		"/api/comments/{contentId}/best",

		/** @brief 문의사항 관련 URL */
		"/api/inquiry/{inquiryId}/comment",
		"/api/inquiry/comment/{inquiryCommentId}",
		"/api/inquiry/{inquiryId}/best/comment",

		/** @brief 공지사항 관련 URL */
		"/api/notice/{noticeId}",
		"/api/notice",
		"/api/notice/comment/{noticeCommentId}",
		"/api/notice/{noticeId}/comment",
		"/api/notice/{noticeId}/best/comment",

		/** @brief 개선요청 관련 URL */
		"/api/improvement/comment/{improvementCommentId}",
		"/api/improvement/{improvementId}/comment",
		"/api/improvement/{improvementId}",
		"/api/improvement",
		"/api/improvement/{improvementId}/best/comment",

		//뉴스
		"/api/news",
		"/api/news/{newId}",
		"/api/news/comment",
		"/api/news/comment/best/{newsId}",
		"/api/news/comment/{newsCommentId}",
		"/api/news/reply",
		"/api/news/reply/{newReplyId}",

		//경기일정
		"/api/match/schedule/{matchCategory}",
		"/api/match/{matchId}",
		"/api/match/prediction/{matchId}",
		"/api/match/esports/youtube",

		/** @brief 게임 할인, 이벤트 관련 URL */
		"api/game/event",
		"api/game/discount",

		/** @brief 홈 메인*/
		"/api/home/new",
		"/api/home/hot",
		"/api/home/search",

		/** @brief 댓글 */
		"/api/comments/{contentId}",
		"/api/comments/{contentId}/best",
	};

	/* Admin 접근 권한 */
	private static final String[] PERMIT_ADMIN_URLS = new String[] {
		// Test Endpoints
		/** @brief Check Access Admin */"/test/admin/**",

		"/api/admin/**",
		"/api/inquiries/answers/**",
	};

	private static final String[] ADMIN_POST_URLS = {
		"/api/notice",
		"/api/improvement/{improvementId}"
	};

	private static final String[] ADMIN_PUT_URLS = {
		"/api/notice/{noticeId}"
	};

	private static final String[] ADMIN_DELETE_URLS = {
		"/api/notice/{noticeId}"
	};

	/* member 접근 권한 */
	private static final String[] PERMIT_MEMBER_URLS = new String[] {
		// Test Endpoints
		/** @brief Check Access Member */"/test/cert",
	};

	@Value("${FRONT_URL:http://localhost:3000}")
	private String frontUrl;
	private final JwtProvider jwtProvider;
	private final CustomUserDetailsService customUserDetailsService;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomOauth2SuccessHandler customOauth2SuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
	private final ApplicationEventPublisher eventPublisher;
	private final RedisService redisService;
	private final MemberJpaRepository memberJpaRepository;

	@PostConstruct
	public void init() {
		log.debug("init security config");
		log.debug("frontUrl = {}", frontUrl);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		log.debug("BCryptPasswordEncoder 빈 등록됨");
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
		// HTTP 헤더 설정
		http.headers(headers -> headers
			.httpStrictTransportSecurity(HstsConfig::disable) // HSTS 비활성화
			.frameOptions(FrameOptionsConfig::disable)        // FrameOptions 비활성화
		);

		// 기본 보안 설정 비활성화
		http.logout(AbstractHttpConfigurer::disable) // 로그아웃 비활성화
			.csrf(AbstractHttpConfigurer::disable) // csrf disable
			.formLogin(AbstractHttpConfigurer::disable) // From 로그인 방식 disable
			.httpBasic(AbstractHttpConfigurer::disable); // HTTP Basic 인증 방식 disable

		// 세션 관리: Stateless
		http.sessionManagement(session -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		// OAuth2 로그인 설정
		http.oauth2Login(oauth2 -> oauth2
			.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
			.successHandler(customOauth2SuccessHandler)
			.failureHandler(oAuth2LoginFailureHandler)
				.authorizationEndpoint(authorization -> authorization
						.authorizationRequestResolver(new CustomAuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization"
						)
					)
				)
		);

		// JWT 인증 및 토큰 검증 필터 추가
		http.addFilterAt(
				new JwtAuthenticationFilter(authenticationManager(), jwtProvider, eventPublisher, redisService),
				UsernamePasswordAuthenticationFilter.class
			)
			.addFilterAfter(new TokenAuthenticationFilter(jwtProvider, memberJpaRepository), JwtAuthenticationFilter.class);
		//                .addFilter(webConfig.corsFilter()); // CORS 필터 추가

		//        // cors 설정
		http.cors((corsCustomizer) -> corsCustomizer.configurationSource(configurationSource()));

		// 예외 처리 핸들러 설정
		http.exceptionHandling(exceptionHandling -> exceptionHandling
			.authenticationEntryPoint(new AuthenticationEntryPointHandler())
			.accessDeniedHandler(new CustomAccessDeniedHandler())
		);

		// 로그아웃 설정
		http.logout(logout -> logout
			.logoutUrl("/logout")
			.invalidateHttpSession(true)
			.logoutSuccessHandler(new LogoutSuccessHandler(jwtProvider))
			.permitAll()
		);

		// 경로별 인가 작업
		http.authorizeHttpRequests(authorizeRequests ->
			authorizeRequests
				.requestMatchers(PERMIT_ALL_URLS).permitAll()
				.requestMatchers(HttpMethod.GET, PUBLIC_GET_URLS).permitAll() // 전체 접근 가능한 endpoint

				.requestMatchers(PERMIT_ADMIN_URLS).hasAnyAuthority(MemberRole.ADMIN.name())
				.requestMatchers(HttpMethod.POST, ADMIN_POST_URLS).hasAuthority(MemberRole.ADMIN.name())
				.requestMatchers(HttpMethod.PUT, ADMIN_PUT_URLS).hasAuthority(MemberRole.ADMIN.name())
				.requestMatchers(HttpMethod.DELETE, ADMIN_DELETE_URLS)
				.hasAuthority(MemberRole.ADMIN.name()) // 관리자만 접근 가능한 endpoint

				.anyRequest().authenticated()                   // 나머지 요청은 모두 허용
		);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(customUserDetailsService);
		return new ProviderManager(provider);
	}

	public CorsConfigurationSource configurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.addAllowedOriginPattern(frontUrl); // TODO_ 추후 변경 해야함 배포시
		configuration.addAllowedOriginPattern("http://localhost:3000"); // TODO_ 추후 변경 해야함 배포시
		configuration.addAllowedOriginPattern("https://main.dbbilwoxps3tu.amplifyapp.com");
		configuration.addAllowedOriginPattern("https://playhive.co.kr");
		configuration.setAllowCredentials(true);
		configuration.addExposedHeader(HEADER_AUTHORIZATION);
		configuration.addExposedHeader(REFRESH_TOKEN_KEY);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
