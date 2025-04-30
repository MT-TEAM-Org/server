package org.myteam.server.match.matchPrediction.dto.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchPredictionServiceRequest {

	private Long matchPredictionId;
	private Side side;

	@Builder
	public MatchPredictionServiceRequest(Long matchPredictionId, Side side) {
		this.matchPredictionId = matchPredictionId;
		this.side = side;
	}
}
