package org.myteam.server.global.util.redis.service;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.myteam.server.admin.utill.StaticDataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService { // TODO: RedisReportService 로 변경.

	private final RedisTemplate<String, String> redisTemplate;
	private static final String BOARD_RANK_KEY=":BoardRank";
	private static final int ADMIN_LOGIN_MAX_REQUESTS=10;
	private static final int MAX_REQUESTS = 3; // 제한 횟수 (기본값: 5분 동안 3회)
	private static final long EXPIRED_TIME = 5L; // 만료 시간 (5분)
	private static final long YOUTUBE_EXPIRED_TIME = 3L * 60L * 60L * 1000L;
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30;      // 30일
	private static final long ADMIN_ALARM_READ_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30;
	private static final String REFRESH_TOKEN_KEY = "refreshToken:";
	private static final String ESPORTS_YOUTUBE_VIDEOID_KEY = "esports:youtube:videoId";

	/**
	 * 특정 키(식별자)에 대한 요청이 제한 범위를 초과했는지 확인
	 *
	 * @param category   제한 기준 (예: "IP", "USER", "ENDPOINT")
	 * @param identifier 제한할 대상 (예: "192.168.1.1", "user-12345")
	 * @return true = 요청 가능 / false = 차단됨
	 */
	public boolean isAllowed(String category, String identifier) {
		String redisKey = getRateLimitKey(category, identifier);

		// 현재 요청 횟수 조회
		String requestCountStr = redisTemplate.opsForValue().get(redisKey);
		int requestCount = requestCountStr == null ? 0 : Integer.parseInt(requestCountStr);

		// 요청 초과 여부 확인
		if (requestCount >= MAX_REQUESTS) {
			log.warn("🚫 [RateLimit] 요청 차단 - Key: {}, 요청 횟수: {}", redisKey, requestCount);
			return false;
		}

		// 요청 횟수 증가
		long newCount = redisTemplate.opsForValue().increment(redisKey);

		// TTL(만료 시간)이 없으면 5분 설정
		if (newCount == 1) {
			redisTemplate.expire(redisKey, Duration.ofMinutes(EXPIRED_TIME));
		}

		log.info("✅ [RateLimit] 요청 허용 - Key: {}, 요청 횟수: {}", redisKey, newCount);
		return true;
	}


	public boolean isAdminLoginAllowed(String category,String identifier){
		String redisKey = getRateLimitKey(category, identifier);
		String requestCountStr = redisTemplate.opsForValue().get(redisKey);
		int requestCount = requestCountStr == null ? 0 : Integer.parseInt(requestCountStr);

		if (requestCount >= ADMIN_LOGIN_MAX_REQUESTS) {
			log.warn("🚫 [RateLimit] 관리자 요청 차단 - Key: {}, 요청 횟수: {}", redisKey, requestCount);
			return false;
		}

		long newCount = redisTemplate.opsForValue().increment(redisKey);

		log.info("✅ [RateLimit] 관리자 요청 허용 - Key: {}, 요청 횟수: {}", redisKey, newCount);
		return true;

	}

	public boolean AdminReadCheck(String category, String adminIdentifier, StaticDataType staticDataType, Long contentId){

		String redisKey=getRateLimitKey(category,adminIdentifier+staticDataType.name()+String.valueOf(contentId));
		String requestCountStr=redisTemplate.opsForValue().get(redisKey);
		int requestCount = requestCountStr == null ? 0 : Integer.parseInt(requestCountStr);
		if(requestCount==0){
			return false;
		}
		return true;
	}

	public void adminReadCheckUpdate(String category, String adminIdentifier, StaticDataType staticDataType, Long contentId){
		String redisKey=getRateLimitKey(category,adminIdentifier+staticDataType.name()+String.valueOf(contentId));
		String requestCountStr=redisTemplate.opsForValue().get(redisKey);
		int requestCount = requestCountStr == null ? 0 : Integer.parseInt(requestCountStr);
		if(requestCount==0) {
			redisTemplate.opsForValue().increment(redisKey);
			redisTemplate.expire(redisKey, Duration.ofMinutes(ADMIN_ALARM_READ_EXPIRE_TIME));
		}
	}
	public void boardRecommendRankPerDay(Long contentId,Long delta){
		LocalDateTime now = LocalDateTime.now().with(LocalTime.MIDNIGHT);
		String key = now.toLocalDate().toString() +BOARD_RANK_KEY;
		LocalDateTime nextMidnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
		redisTemplate.opsForZSet().incrementScore(key,contentId.toString(),delta);
		redisTemplate.expireAt(key,Date.valueOf(nextMidnight.toLocalDate()));
	}
	public List<Long> getBoardRecommendRankPerDay(){
		LocalDateTime now=LocalDateTime.now().with(LocalTime.MIDNIGHT);
		String key=now+BOARD_RANK_KEY;
		List<Long> ids=redisTemplate.opsForZSet().reverseRange(key,0,9)
				.stream()
				.filter(x->{
					if(Long.parseLong(x)>0){
						return true;
					}
					return false;
				})
				.map(x->{
					return Long.parseLong(x);
				})
				.collect(Collectors.toList());
		return ids;
	}

	/**
	 * 요청 제한을 적용할 Redis Key 생성
	 *
	 * @param category   제한 기준 (예: "IP", "USER", "ENDPOINT")
	 * @param identifier 제한할 대상 (예: "192.168.1.1", "user-12345")
	 * @return Redis에 저장할 Key
	 */
	private String getRateLimitKey(String category, String identifier) {
		return "rate_limit:" + category + ":" + identifier;
	}

	/**
	 * 특정 키의 현재 요청 횟수 조회
	 */
	public int getRequestCount(String category, String identifier) {
		String redisKey = getRateLimitKey(category, identifier);
		String requestCountStr = redisTemplate.opsForValue().get(redisKey);

		int count = requestCountStr == null ? 0 : Integer.parseInt(requestCountStr);
		log.debug("🔍 [Redis] 요청 횟수 조회 - Key: {}, Count: {}", redisKey, count);

		return count;
	}

	/**
	 * 특정 키의 제한 시간(TTL) 조회
	 */
	public long getTimeToLive(String category, String identifier) {
		String redisKey = getRateLimitKey(category, identifier);
		Long ttl = redisTemplate.getExpire(redisKey);

		long remainingTime = ttl != null ? ttl : -1;
		log.debug("⏳ [Redis] TTL 조회 - Key: {}, 남은 TTL: {} 초", redisKey, remainingTime);

		return remainingTime;
	}

	/**
	 * 특정 키의 요청 제한 초기화 (삭제)
	 */
	public void resetRequestCount(String category, String identifier) {
		String redisKey = getRateLimitKey(category, identifier);

		if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
			redisTemplate.delete(redisKey);
			log.debug("🗑️ [Redis] 요청 횟수 초기화 - Key: {}", redisKey);
		} else {
			log.debug("⚠️ [Redis] 초기화 실패 - Key: {} (존재하지 않음)", redisKey);
		}
	}

	public String getEsportsYoutubeVideoId() {
		return redisTemplate.opsForValue().get(ESPORTS_YOUTUBE_VIDEOID_KEY);
	}

	public void putEsportsYoutubeVideoId(String esportsYoutubeUrl) {
		redisTemplate.opsForValue()
			.set(ESPORTS_YOUTUBE_VIDEOID_KEY, esportsYoutubeUrl, Duration.ofMillis(YOUTUBE_EXPIRED_TIME));
	}

	public void putRefreshToken(UUID publicId, String refreshToken) {
		redisTemplate.opsForValue()
			.set(REFRESH_TOKEN_KEY + publicId, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
	}

	public String getRefreshToken(UUID publicId) {
		return redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY + publicId);
	}

	public void deleteRefreshToken(UUID publicId) {
		redisTemplate.delete(REFRESH_TOKEN_KEY + publicId);
	}
}
