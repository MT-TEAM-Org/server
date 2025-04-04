package org.myteam.server.global.util.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    /**
     * 쿠키 생성
     *
     * @param key        쿠키 키
     * @param value      쿠키 값
     * @param maxAge     쿠키 유효시간 (초 단위)
     * @param path       쿠키 경로
     * @param isHttpOnly JavaScript 에서 접근 불가능하도록 설정
     * @return Cookie
     */
    public static Cookie createCookie(String key, String value, String path, int maxAge, boolean isHttpOnly, String domain) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        // cookie.setSecure(isSecure); // Secure 설정 여부 (HTTPS에서만 사용)
        cookie.setPath(path);
        cookie.setHttpOnly(isHttpOnly);
        if (domain != null) {
            // ex) playhive.shop
            cookie.setDomain("." + domain);
        }
        return cookie;
    }

    /**
     * 쿠키 생성
     *
     * @param key        쿠키 키
     * @param value      쿠키 값
     * @param maxAge     쿠키 유효시간 (초 단위)
     * @param isHttpOnly JavaScript 에서 접근 불가능하도록 설정
     * @return Cookie
     */
    public static Cookie createCookie(String key, String value, int maxAge, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        // cookie.setSecure(isSecure); // Secure 설정 여부 (HTTPS에서만 사용)
        cookie.setPath("/");
        cookie.setHttpOnly(isHttpOnly);
        return cookie;
    }

    /**
     * 쿠키 조회
     *
     * @param request HttpServletRequest
     * @param key     찾고자 하는 쿠키 키
     * @return Optional<Cookie>
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String key) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(key))
                    .findFirst();
        }
        return Optional.empty();
    }

    /**
     * 쿠키 삭제
     *
     * @param key 쿠키 키
     * @return Cookie (삭제 처리된 쿠키)
     */
    public static Cookie deleteCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/"); // 기존 쿠키 경로와 동일하게 설정
        return cookie;
    }

    /**
     * 응답에 쿠키 삭제 추가
     *
     * @param response HttpServletResponse
     * @param key      삭제할 쿠키 키
     */
    public static void deleteCookie(HttpServletResponse response, String key) {
        Cookie cookie = deleteCookie(key);
        response.addCookie(cookie);
    }
}
