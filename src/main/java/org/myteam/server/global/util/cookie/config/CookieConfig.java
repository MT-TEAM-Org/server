package org.myteam.server.global.util.cookie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class CookieConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setDomainName(".playhive.co.kr"); // ✅ 서브도메인 전체에서 쿠키 공유
        serializer.setCookiePath("/");               // ✅ 전체 경로에 적용
        serializer.setSameSite("None");              // ✅ 크로스 도메인 허용 (필수)
        serializer.setUseSecureCookie(true);         // ✅ HTTPS에서만 전달
        serializer.setUseHttpOnlyCookie(true);       // ✅ JS 접근 방지 (선택)
        return serializer;
    }
}
