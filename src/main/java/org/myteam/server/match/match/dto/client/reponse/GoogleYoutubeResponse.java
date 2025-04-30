package org.myteam.server.match.match.dto.client.reponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleYoutubeResponse {

	@JsonProperty("kind")
	private String kind;
	@JsonProperty("etag")
	private String etag;
	@JsonProperty("regionCode")
	private String regionCode;
	@JsonProperty("items")
	private List<Item> items;

	@NoArgsConstructor
	public static class Item {

		@JsonProperty("id")
		private Id id;

		@Builder
		public Item(Id id) {
			this.id = id;
		}
	}

	public static class Id {
		@JsonProperty("videoId")
		private String videoId;
	}

	@Builder
	public GoogleYoutubeResponse(List<Item> items) {
		this.items = items;
	}

	public boolean isLive() {
		return !items.isEmpty();
	}

	public String getVideoId() {
		return items.get(0).id.videoId;
	}
}
