package org.myteam.server.match.match.client;

import org.myteam.server.global.config.FeignConfig;
import org.myteam.server.match.match.dto.client.reponse.GoogleYoutubeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "${google.api.name}", url = "${google.api.url}", configuration = FeignConfig.class)
public interface GoogleFeignClient {

	@GetMapping(value = "/youtube/v3/search", consumes = MediaType.APPLICATION_JSON_VALUE)
	GoogleYoutubeResponse searchLiveVideos(
		@RequestParam("part") String part,
		@RequestParam("channelId") String channelId,
		@RequestParam("eventType") String eventType,
		@RequestParam("type") String type,
		@RequestParam("key") String apiKey
	);

}
