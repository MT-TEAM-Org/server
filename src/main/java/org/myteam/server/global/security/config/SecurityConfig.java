package org.myteam.server.global.security.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.auth.repository.RefreshJpaRepository;
import org.myteam.server.global.config.WebConfig;
import org.myteam.server.global.security.filter.AuthenticationEntryPointHandler;
import org.myteam.server.global.security.filter.CustomAccessDeniedHandler;
import org.myteam.server.global.security.filter.JwtAuthenticationFilter;
import org.myteam.server.global.security.filter.TokenAuthenticationFilter;
import org.myteam.server.global.security.handler.LogoutSuccessHandler;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.security.service.CustomUserDetailsService;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.oauth2.handler.CustomOauth2SuccessHandler;
import org.myteam.server.oauth2.handler.OAuth2LoginFailureHandler;
import org.myteam.server.oauth2.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.HstsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.myteam.server.auth.controller.ReIssueController.TOKEN_REISSUE_PATH;
import static org.myteam.server.global.security.jwt.JwtProvider.HEADER_AUTHORIZATION;
import static org.myteam.server.global.security.jwt.JwtProvider.REFRESH_TOKEN_KEY;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /* 권한 제외 대상 */
    private static final String[] PERMIT_ALL_URLS = new String[]{
            // Test Endpoints
            /** @brief Exception Test */"/test/exception-test",
            /** @brief Can Access All */"/test/all/**",
            /** @brief Test login, create */"/api/test/**",
            /** @brief Test Slack Integration */"/test/slack",
            "/api/members/get-token/**", "/api/attachments/**", "/api/posts/**",

            // Chat
            "/ws-stomp/**",

            // Health Check
            /** @brief health check */"/status",

            // Swagger Documents
            /** @brief Swagger Docs */"/v3/api-docs/**", "/swagger-ui/**",

            // Database console
            /** @brief database url */"/h2-console",

            // Business Logic
            /** @brief about login */"/auth/**",
            /** @brief Allow static resource access */"/upload/**",
            /** @brief Allow user permission to change */"/api/members/role",
            "/api/certification/send",
            "/api/certification/certify-code",
            "/api/oauth2/members/email/**",
            "/api/members/type/**",
            "/api/me/create",
            TOKEN_REISSUE_PATH,

            // 문의하기
            "/api/inquiries/create",
    };
    /* Admin 접근 권한 */
    private static final String[] PERMIT_ADMIN_URLS = new String[]{
            // Test Endpoints
            /** @brief Check Access Admin */"/test/admin/**",

            "/api/admin/**",
    };
    /* member 접근 권한 */
    private static final String[] PERMIT_MEMBER_URLS = new String[]{
            // Test Endpoints
            /** @brief Check Access Member */"/test/cert",
    };

    @Value("${FRONT_URL:http://localhost:3000}")
    private String frontUrl;
    private final JwtProvider jwtProvider;
    private final WebConfig webConfig;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOauth2SuccessHandler customOauth2SuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final RefreshJpaRepository refreshJpaRepository;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // HTTP 헤더 설정
        http.headers(headers -> headers
                .httpStrictTransportSecurity(HstsConfig::disable) // HSTS 비활성화
                .frameOptions(FrameOptionsConfig::disable)        // FrameOptions 비활성화
        );

        // 기본 보안 설정 비활성화
        http.logout((auth) -> auth.disable()) // 로그아웃 비활성화
                .csrf((auth) -> auth.disable()) // csrf disable
                .formLogin((auth) -> auth.disable()) // From 로그인 방식 disable
                .httpBasic((auth) -> auth.disable()); // HTTP Basic 인증 방식 disable

        // 세션 관리: Stateless
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(customOauth2SuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
        );

        // JWT 인증 및 토큰 검증 필터 추가
        http.addFilterAt(
                        new JwtAuthenticationFilter(authenticationManager(), jwtProvider, refreshJpaRepository),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterAfter(new TokenAuthenticationFilter(jwtProvider), JwtAuthenticationFilter.class)
                .addFilter(webConfig.corsFilter()); // CORS 필터 추가

//        // cors 설정
//        http.cors((corsCustomizer) -> corsCustomizer.configurationSource(configurationSource()));

        // 예외 처리 핸들러 설정
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new AuthenticationEntryPointHandler())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .logoutSuccessHandler(new LogoutSuccessHandler(jwtProvider, refreshJpaRepository))
                .permitAll()
        );

        // 경로별 인가 작업
        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers(PERMIT_ALL_URLS).permitAll()
                        .requestMatchers(PERMIT_ADMIN_URLS).hasAnyAuthority(MemberRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyAuthority(MemberRole.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyAuthority(MemberRole.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/categories").hasAnyAuthority(MemberRole.ADMIN.name())

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
        System.out.println("configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern(frontUrl); // TODO_ 추후 변경 해야함 배포시
        configuration.addAllowedOriginPattern("http://localhost:3000"); // TODO_ 추후 변경 해야함 배포시
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader(HEADER_AUTHORIZATION);
        configuration.addExposedHeader(REFRESH_TOKEN_KEY);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
