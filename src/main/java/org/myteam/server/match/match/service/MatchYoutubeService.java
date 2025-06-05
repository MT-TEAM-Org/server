package org.myteam.server.match.match.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.match.match.client.GoogleFeignClient;
import org.myteam.server.match.match.dto.client.reponse.GoogleYoutubeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchYoutubeService {

	private static final String part = "snippet";
	private static final String channelId = "UCw1DsweY9b2AKGjV4kGJP1A";
	private static final String eventType = "live";
	private static final String type = "video";

	private final GoogleFeignClient googleFeignClient;
	private final RedisService redisService;

	@Value("${google.api.apiKey}")
	private String apiKey;

	public String getVideoId() {
		String videoId = redisService.getEsportsYoutubeVideoId();
		if (videoId == null) {
			videoId = getUrlToGoogleApi();
			if (videoId != null) {
				redisService.putEsportsYoutubeVideoId(videoId);
			} else {
				throw new PlayHiveException(ErrorCode.API_SERVER_ERROR);
			}
		}
		return videoId;
	}

	private String getUrlToGoogleApi() {
		GoogleYoutubeResponse googleYoutubeResponse = googleFeignClient.searchLiveVideos(part,
			channelId, eventType, type, apiKey);
		if (googleYoutubeResponse.isLive()) {
			return googleYoutubeResponse.getVideoId();
		}
		return null;
	}
}
