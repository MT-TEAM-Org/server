package org.myteam.server.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.myteam.server.global.security.jwt.JwtProvider.HEADER_AUTHORIZATION;
import static org.myteam.server.global.security.jwt.JwtProvider.REFRESH_TOKEN_KEY;

@Configuration
public class WebConfig {

    @Value("${FRONT_URL:http://localhost:3000}")
    private String frontUrl;
    @Value("${BACKEND_URL:http://localhost:8080}")
    private String backendUrl;

    protected WebConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        final String[] ALLOWED_ORIGIN = {frontUrl, backendUrl};

        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(ALLOWED_ORIGIN));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.addExposedHeader(HEADER_AUTHORIZATION);
        config.addExposedHeader(REFRESH_TOKEN_KEY);

        source.registerCorsConfiguration("/**", config);

        // TODO: 타입 확인해보기
        return new CorsFilter(source);
    }
}
