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
    private static final String[] permitAllUrl = new String[]{
            /** @brief test */"/test/exception-test",
            /** @brief Swagger Docs */"/v3/api-docs/**", "/swagger-ui/**",
            /** @brief database url */"/h2-console",
            /** @brief about login */"/auth/**",
    };
    /* Admin 접근 권한 */
    private static final String[] permitAdminUrl = new String[]{
            /** @brief Check Access Admin */"/test/manager-access-test/**",
    };
    /* member 접근 권한 */
    private static final String[] permitMemberUrl = new String[]{
            "/test/user-access-test/**",
    };

    @Value("${FRONT_URL:http://playhive.shop:3000}")
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
        http
                // HTTP 헤더 설정
                .headers(headers ->
                        headers
                                .httpStrictTransportSecurity(HstsConfig::disable) // HSTS 비활성화
                                .frameOptions(FrameOptionsConfig::disable)        // FrameOptions 비활성화
                );

        // 로그아웃 비활성화
        http
                .logout((auth) -> auth.disable());

        // csrf disable
        http
                .csrf((auth) -> auth.disable());

        // From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customOauth2SuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                );

        http
                .addFilterAt(
                        new JwtAuthenticationFilter(authenticationManager(), jwtProvider, refreshJpaRepository),
                        UsernamePasswordAuthenticationFilter.class
                ) // 로그인 인증 필터
                .addFilterAfter(new TokenAuthenticationFilter(jwtProvider), JwtAuthenticationFilter.class)
                .addFilter(webConfig.corsFilter()); // JWT 토큰 검증 필터

        // cors 설정
        http
                .cors((corsCustomizer) -> corsCustomizer.configurationSource(configurationSource()));

        // 경로별 인가 작업
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/upload/**").permitAll()       // 정적 자원 접근 허용
                                .requestMatchers(permitAllUrl).permitAll()
                                .requestMatchers(permitAdminUrl).hasRole("ADMIN")

                                .requestMatchers("/h2-console").permitAll()       // H2 콘솔 접근 허용
                                .requestMatchers("/api/members/get-token/**").permitAll()       // 테스트용 토큰 발급용
                                .requestMatchers("/api/attachments/**").permitAll()       // 테스트용
                                .requestMatchers("/api/posts/**").permitAll()       // 테스트용

                                .requestMatchers("/api/admin/**").hasAnyAuthority(MemberRole.ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/api/certification/send").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/certification/certify-code").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/oauth2/members/email/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/members/type/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/me/create").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyAuthority(MemberRole.ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyAuthority(MemberRole.ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/api/categories").hasAnyAuthority(MemberRole.ADMIN.name())

                                .requestMatchers(HttpMethod.POST, TOKEN_REISSUE_PATH).permitAll()          // 토큰 재발급
                                .requestMatchers("/api/members/role").permitAll()       // 유저 권한 변경 허용

                                .anyRequest().authenticated()                   // 나머지 요청은 모두 허용
                );

        http
                .exceptionHandling(errorHandling ->
                        errorHandling
                                .authenticationEntryPoint(new AuthenticationEntryPointHandler())
                                .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        // 로그아웃 처리
        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(new LogoutSuccessHandler(jwtProvider, refreshJpaRepository))
                        .permitAll());

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
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader(HEADER_AUTHORIZATION);
        configuration.addExposedHeader(REFRESH_TOKEN_KEY);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
