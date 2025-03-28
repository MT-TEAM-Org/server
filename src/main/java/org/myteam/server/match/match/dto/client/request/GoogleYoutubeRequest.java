package org.myteam.server.match.match.dto.client.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleYoutubeRequest {
	private String part;
	private String channelId;
	private String eventType;
	private String type;
	private String key;
}
