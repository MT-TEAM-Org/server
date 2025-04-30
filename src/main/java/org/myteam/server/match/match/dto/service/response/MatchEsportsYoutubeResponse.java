package org.myteam.server.match.match.dto.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchEsportsYoutubeResponse {
	private boolean isLive;
	private String videoId;

	@Builder
	public MatchEsportsYoutubeResponse(boolean isLive, String videoId) {
		this.isLive = isLive;
		this.videoId = videoId;
	}

	public static MatchEsportsYoutubeResponse createResponse(boolean isLive, String videoId) {
		return MatchEsportsYoutubeResponse.builder()
			.isLive(isLive)
			.videoId(videoId)
			.build();
	}
}
