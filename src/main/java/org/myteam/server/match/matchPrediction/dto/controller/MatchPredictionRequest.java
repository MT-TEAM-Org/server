package org.myteam.server.match.matchPrediction.dto.controller;

import org.myteam.server.match.matchPrediction.dto.service.request.MatchPredictionServiceRequest;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchPredictionRequest {

	private Long matchPredictionId;
	private Side side;

	@Builder
	public MatchPredictionRequest(Long matchPredictionId, Side side) {
		this.matchPredictionId = matchPredictionId;
		this.side = side;
	}

	public MatchPredictionServiceRequest toServiceRequest() {
		return MatchPredictionServiceRequest.builder()
			.matchPredictionId(matchPredictionId)
			.side(side)
			.build();
	}
}
