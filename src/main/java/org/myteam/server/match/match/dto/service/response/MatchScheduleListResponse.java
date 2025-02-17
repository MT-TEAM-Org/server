package org.myteam.server.match.match.dto.service.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchScheduleListResponse {

	private List<MatchResponse> list;

	@Builder
	public MatchScheduleListResponse(List<MatchResponse> list) {
		this.list = list;
	}

	public static MatchScheduleListResponse createResponse(List<MatchResponse> list) {
		return MatchScheduleListResponse.builder()
			.list(list)
			.build();
	}
}
