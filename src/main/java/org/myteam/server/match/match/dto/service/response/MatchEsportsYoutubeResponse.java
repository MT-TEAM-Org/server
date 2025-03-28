package org.myteam.server.match.match.dto.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchEsportsYoutubeResponse {
	private boolean isLive;
	private String url;

	@Builder
	public MatchEsportsYoutubeResponse(boolean isLive, String url) {
		this.isLive = isLive;
		this.url = url;
	}

	public static MatchEsportsYoutubeResponse createResponse(boolean isLive, String url) {
		return MatchEsportsYoutubeResponse.builder()
			.isLive(isLive)
			.url(url)
			.build();
	}
}
