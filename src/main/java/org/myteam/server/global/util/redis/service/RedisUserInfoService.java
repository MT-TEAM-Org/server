package org.myteam.server.global.util.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.info.domain.UserInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisUserInfoService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "token:";
    private static final int LIMIT_DAYS = 1;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveUserInfo(String token, UserInfo userInfo) {
        try {
            String key = PREFIX + token;
            String value = objectMapper.writeValueAsString(userInfo);

            redisTemplate.opsForValue().set(key, value, Duration.ofDays(LIMIT_DAYS));
            log.info("Saved user info to Redis. Key={}, TTL={}", key, Duration.ofDays(LIMIT_DAYS));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize userInfo", e);
        }
    }

    /**
     * 채팅서버에서 사용
     * @param token
     */
//    public Optional<UserInfo> getUserInfo(String token) {
//        String key = PREFIX + token;
//        try {
//            String value = (String) redisTemplate.opsForValue().get(key);
//            if (value == null) return Optional.empty();
//            return Optional.of(objectMapper.readValue(value, UserInfo.class));
//        } catch (Exception e) {
//            log.error("Failed to deserialize userInfo", e);
//            return Optional.empty();
//        }
//    }

    public void deleteUserInfo(String token) {
        String key = PREFIX + token;
        redisTemplate.delete(key);
        log.info("Deleted user info from Redis. Key={}", key);
    }
}
