package org.myteam.server.chat.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.service.BanService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final BanService banService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        StompCommand command = accessor.getCommand();
        if (command == StompCommand.CONNECT) {
            String authorizationHeader = getAuthorizationHeader(accessor);

            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                log.warn("Authorization header missing or not Bearer type: {}", authorizationHeader);
                throw new PlayHiveException(ErrorCode.MISSING_AUTH_HEADER);
            }

            handleConnect(accessor, authorizationHeader);
        } else if (command == StompCommand.ERROR) {
            // ERROR 프레임의 경우
            throw new PlayHiveException(ErrorCode.INVALID_TOKEN);
        }

        return message;
    }

    /**
     * STOMP CONNECT 프레임 처리 로직
     */
    private void handleConnect(StompHeaderAccessor accessor, String authorizationHeader) {

        if (!authorizationHeader.startsWith("[Bearer ")) {
            log.warn("Authorization header missing or not Bearer type: {}", authorizationHeader);
            throw new PlayHiveException(ErrorCode.MISSING_AUTH_HEADER);
        }

        String token = extractToken(authorizationHeader);

        if (!jwtProvider.validToken(token)) {
            log.warn("Invalid JWT token: {}", token);
            throw new PlayHiveException(ErrorCode.INVALID_TOKEN);
        }

        Authentication authentication = jwtProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        accessor.setUser(authentication);

        String username = accessor.getUser() != null ? accessor.getUser().getName() : null;
        if (banService.isBannedUser(username)) {
            log.warn("Banned user tried to connect: {}", username);
            throw new PlayHiveException(ErrorCode.BAN_USER);
        }

        log.info("Authenticated user connected: {}", username);
    }

    /**
     * STOMP 헤더에서 "Authorization" 값을 얻는다.
     */
    private String getAuthorizationHeader(StompHeaderAccessor accessor) {
        String authHeaders = String.valueOf(accessor.getNativeHeader("Authorization"));
        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }
        return authHeaders;
    }

    /**
     * "[Bearer xxxxx]" 형태에서 실제 토큰 부분만 추출
     */
    private String extractToken(String authorizationHeader) {
        String[] parts = authorizationHeader.split(" ");
        if (parts.length < 2) {
            throw new PlayHiveException(ErrorCode.MISSING_AUTH_HEADER);
        }

        String rawToken = parts[1];
        return rawToken.substring(0, rawToken.length() - 1); // "]" 제거
    }
}