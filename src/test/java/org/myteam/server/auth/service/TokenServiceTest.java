package org.myteam.server.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.TestContainerSupport;
import org.myteam.server.global.exception.PlayHiveJwtException;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.util.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.myteam.server.global.exception.ErrorCode.EXPIRED_REFRESH_TOKEN;
import static org.myteam.server.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static org.myteam.server.global.security.jwt.JwtProvider.HEADER_AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest extends IntegrationTestSupport {

    @Autowired
    private TokenService tokenService;
    @MockBean
    private JwtProvider jwtProvider;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Captor
    private ArgumentCaptor<String> tokenCaptor;

    private final UUID mockPublicId = UUID.randomUUID();
    private final String mockAccessToken = "mockAccessToken";
    private final String mockRefreshToken = "mockRefreshToken";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Captor 초기화
    }

    @Test
    @DisplayName("정상적으로 AccessToken을 재발급한다.")
    void regenerateAccessToken_정상_케이스() {
        // given
        when(request.getHeader(HEADER_AUTHORIZATION)).thenReturn("Bearer " + mockAccessToken);
        when(jwtProvider.getAccessToken(anyString())).thenReturn(mockAccessToken);
        when(jwtProvider.getPublicIdWithoutExpired(mockAccessToken)).thenReturn(mockPublicId);
        when(redisService.getRefreshToken(mockPublicId)).thenReturn(mockRefreshToken);

        when(jwtProvider.isExpired(mockRefreshToken)).thenReturn(false);
        when(jwtProvider.getPublicId(mockRefreshToken)).thenReturn(mockPublicId);
        when(jwtProvider.getRole(mockRefreshToken)).thenReturn("USER");
        when(jwtProvider.getStatus(mockRefreshToken)).thenReturn("ACTIVE");
        when(jwtProvider.generateToken(any(), any(), any(), anyString(), anyString())).thenReturn("newAccessToken");

        // when
        tokenService.regenerateAccessToken(request, response);

        // then
        verify(response).addHeader(eq("Authorization"), eq("Bearer newAccessToken"));
    }

    @Test
    @DisplayName("리프레시 토큰이 존재하지 않으면 예외가 발생한다.")
    void regenerateAccessToken_리프레시_토큰_없음() {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + mockAccessToken);
        when(jwtProvider.getAccessToken(anyString())).thenReturn(mockAccessToken);
        when(jwtProvider.getPublicIdWithoutExpired(mockAccessToken)).thenReturn(mockPublicId);
        when(redisService.getRefreshToken(mockPublicId)).thenReturn(null);

        // when
        PlayHiveJwtException exception = assertThrows(PlayHiveJwtException.class, () -> {
            tokenService.regenerateAccessToken(request, response);
        });

        // then
        assertEquals(INVALID_REFRESH_TOKEN, exception.getErrorCode());
    }

    @Test
    @DisplayName("리프레시 토큰이 만료되었으면 예외가 발생한다.")
    void regenerateAccessToken_리프레시_토큰_만료() {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + mockAccessToken);
        when(jwtProvider.getAccessToken(anyString())).thenReturn(mockAccessToken);
        when(jwtProvider.getPublicIdWithoutExpired(mockAccessToken)).thenReturn(mockPublicId);
        when(redisService.getRefreshToken(mockPublicId)).thenReturn(mockRefreshToken);
        when(jwtProvider.isExpired(mockRefreshToken)).thenReturn(true);

        // when
        PlayHiveJwtException exception = assertThrows(PlayHiveJwtException.class, () -> {
            tokenService.regenerateAccessToken(request, response);
        });

        // then
        verify(jwtProvider).isExpired(tokenCaptor.capture());

        assertEquals(EXPIRED_REFRESH_TOKEN, exception.getErrorCode());
    }
}