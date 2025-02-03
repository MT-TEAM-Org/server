package org.myteam.server.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.auth.domain.Tokens;
import org.myteam.server.auth.entity.Refresh;
import org.myteam.server.auth.repository.RefreshJpaRepository;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.myteam.server.global.exception.ErrorCode.*;
import static org.myteam.server.global.security.jwt.JwtProvider.*;
import static org.myteam.server.global.util.cookie.CookieUtil.getCookie;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReIssueService {
    private final JwtProvider jwtProvider;
    private final RefreshJpaRepository refreshJpaRepository;

    /**
     * Refresh Token 검증
     */
    public void validateRefreshToken(String refresh, UUID publicId) {
        log.info("refresh : {}, publicId: {}", refresh, publicId);

        // 리프레시 토큰 만료 여부 체크
        try {
            Boolean expired = jwtProvider.isExpired(refresh);
            if (expired) {
                throw new PlayHiveException(REFRESH_TOKEN_EXPIRED);
            }
        } catch (ExpiredJwtException e) {
            throw new PlayHiveException(INVALID_REFRESH_TOKEN);
        }

        // 리프레시 토큰 카테고리 검증
        String category = jwtProvider.getCategory(refresh);
        if (!category.equals(TOKEN_CATEGORY_REFRESH)) {
            throw new PlayHiveException(INVALID_TOKEN_TYPE);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshJpaRepository.existsByRefreshAndPublicId(refresh, publicId);
        if (!isExist) {
            log.info("기존의 리프레시 토큰이 존재하지 않음");
            throw new PlayHiveException(INVALID_REFRESH_TOKEN);
        }
    }

    /**
     * Refresh Token 정보 추출
     *
     * @param request
     * @return
     */
    public String extractRefreshToken(HttpServletRequest request) {
        // 쿠키로 부터 리프레시 토큰 추출
        Optional<Cookie> refreshTokenOP = getCookie(request, REFRESH_TOKEN_KEY);
        if (refreshTokenOP.isEmpty()) {
            log.error("Refresh token cookie is empty.");
            throw new PlayHiveException(INVALID_REFRESH_TOKEN);
        }

        // URL 디코딩된 리프레시 토큰 값
        String rawToken = refreshTokenOP.get().getValue();
        String refresh = URLDecoder.decode(rawToken, StandardCharsets.UTF_8);
        log.debug("Decoded refresh token: {}", refresh);

        if (refresh == null || refresh.isBlank()) {
            log.error("Decoded refresh token is blank.");
            throw new PlayHiveException(INVALID_REFRESH_TOKEN);
        }

        // 리프레시 토큰에서 액세스 토큰 추출
        return refresh;
    }

    @Transactional
    public Tokens reissueTokens(HttpServletRequest request) {
        try {
            // Refresh Token 추출 및 디코딩
            String refresh = extractRefreshToken(request);

            log.info("Extracted refresh token: {}", refresh);

            // Refresh Token 검증
            UUID publicId = jwtProvider.getPublicId(refresh);
            String role = jwtProvider.getRole(refresh);
            String status = jwtProvider.getStatus(refresh);

            log.info("publicId: {}, role: {}", publicId, role);

            validateRefreshToken(refresh, publicId);

            // 새로운 Access 및 Refresh 토큰 생성
            // Authorization
            String newAccess = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1), publicId, role, status);
            // X-Refresh-Token
            String newRefresh = jwtProvider.generateToken(TOKEN_CATEGORY_REFRESH, Duration.ofHours(24), publicId, role, status);

            log.info("쿠키 확인용 refresh: {}, publicId: {}", refresh, publicId);
            log.info("쿠키 확인용 refresh: {}, publicId: {}", refresh, publicId);

            // 기존 리프레시 토큰 삭제
            deleteByPublicId(publicId);
            // 새로운 리프레시 토큰 등록
            addRefreshEntity(publicId, newRefresh, Duration.ofHours(24));

            return new Tokens(newAccess, newRefresh);
        } catch (PlayHiveException e) {
            // PlayHiveException은 그대로 던짐
            log.error("PlayHiveException 은 그대로 던짐 : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // 기타 예외는 PlayHiveException으로 래핑
            log.error("기타 예외는 PlayHiveException 으로 래핑 : {}", e.getMessage());
            throw new PlayHiveException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void addRefreshEntity(UUID publicId, String refresh, Duration duration) {
        log.info("addRefreshEntity > publicId: {}, refresh: {}, expiredMs: {}", publicId, refresh, duration.toMillis());
        Date date = new Date(System.currentTimeMillis() + duration.toMillis());

        Refresh refreshEntity = Refresh.builder()
                .publicId(publicId)
                .refresh(refresh)
                .expiration(date.toString())
                .build();
        refreshJpaRepository.save(refreshEntity);
    }

    public void deleteByPublicId(UUID publicId) {
        refreshJpaRepository.deleteByPublicId(publicId);
    }
}
