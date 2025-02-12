package org.myteam.server.match.matchSchedule.dto.service.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchScheduleListResponse {

	private List<MatchScheduleResponse> list;

	@Builder
	public MatchScheduleListResponse(List<MatchScheduleResponse> list) {
		this.list = list;
	}

	public static MatchScheduleListResponse createResponse(List<MatchScheduleResponse> list) {
		return MatchScheduleListResponse.builder()
			.list(list)
			.build();
	}
}
