package org.myteam.server.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.myteam.server.global.security.jwt.JwtProvider.HEADER_AUTHORIZATION;
import static org.myteam.server.global.security.jwt.JwtProvider.REFRESH_TOKEN_KEY;

@Configuration
public class WebConfig {

    private final String[] ALLOWED_ORIGIN = {"http://localhost:3000", "https://main.dbbilwoxps3tu.amplifyapp.com",
            "https://playhive.co.kr", "https://www.playhive.co.kr"};

    protected WebConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

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

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowedOrigins(Arrays.asList(ALLOWED_ORIGIN));
        configuration.setAllowCredentials(true);
        
        configuration.addExposedHeader(HEADER_AUTHORIZATION);
        configuration.addExposedHeader(REFRESH_TOKEN_KEY);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
