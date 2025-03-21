package org.myteam.server.global.cookie;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieHandler {

	// 요청에서 쿠키 값을 가져오는 메서드
	public String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// 쿠키에 ID 포함 여부 확인
	public boolean hasPostBeenViewed(Long id, String postViewCookieValue, List<String> postViewList) {
		if (postViewCookieValue != null) {
			postViewList.addAll(Arrays.asList(postViewCookieValue.split("\\|")));
			return postViewList.contains(String.valueOf(id));
		}
		return false;
	}

	// 쿠키 업데이트
	public void updatePostViewCookie(HttpServletResponse response, Long id, boolean hasViewed,
		String postViewCookieName, List<String> postViewList) {
		if (!hasViewed) {
			postViewList.add(String.valueOf(id));
			String updatedCookieValue = String.join("|", postViewList);
			Cookie postViewCookie = new Cookie(postViewCookieName, updatedCookieValue);
			response.addCookie(postViewCookie);
		}
	}
}
