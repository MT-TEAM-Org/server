package org.myteam.server.global.security.filter;

import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static org.myteam.server.global.exception.ErrorCode.*;
import static org.myteam.server.global.security.jwt.JwtProvider.HEADER_AUTHORIZATION;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_ACCESS;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    private static final String[] excludePath = {
        "/api/token/regenerate",
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Token Authenticate Filter 토큰을 검사중");
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String accessToken = jwtProvider.getAccessToken(authorizationHeader);

        log.info("accessToken : " + accessToken);
        if (StringUtils.isNotBlank(accessToken)) {
            try {
                if (!jwtProvider.validToken(accessToken)) {
                    log.warn("인증되지 않은 토큰입니다");
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, INVALID_ACCESS_TOKEN.name());
                    return;
                }

                Boolean expired = jwtProvider.isExpired(accessToken);
                if (expired) {
                    log.warn("토큰이 만료되었습니다.");
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ACCESS_TOKEN_EXPIRED.name());
                    return;
                }

                String accessCategory = jwtProvider.getCategory(accessToken);
                if (!TOKEN_CATEGORY_ACCESS.equals(accessCategory)) {
                    sendErrorResponse(response, HttpStatus.BAD_REQUEST, INVALID_TOKEN_TYPE.name());
                    return;
                }

                UUID publicId = jwtProvider.getPublicId(accessToken);
                String role = jwtProvider.getRole(accessToken);
                String status = jwtProvider.getStatus(accessToken);

                log.info("publicId : " + publicId);
                log.info("role : " + role);
                log.info("status : " + status);

                Member member = Member.builder()
                        .publicId(publicId)
                        .role(MemberRole.valueOf(role))
                        .status(MemberStatus.valueOf(status))
                        .build();

                CustomUserDetails customUserDetails = new CustomUserDetails(member);
                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("SecurityContext 에 인증 정보 저장 완료");
            } catch (JwtException e) {
                log.error("JWT 처리 중 오류 발생: {}", e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


    /**
     * 공통 에러 응답 처리 메서드
     *
     * @param response   HttpServletResponse
     * @param httpStatus HTTP 상태 오브젝트
     * @param message    메시지
     * @throws IOException
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message) throws IOException {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"message\":\"%s\",\"status\":\"%s\"}", message, httpStatus.name()));
    }
}
