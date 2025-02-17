package org.myteam.server.match.matchPrediction.service;

import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.MatchPredictionServiceRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchPredictionService {

	private final MatchPredictionReadService matchPredictionReadService;

	public Long update(MatchPredictionServiceRequest matchPredictionServiceRequest) {
		MatchPrediction prediction = matchPredictionReadService.findByIdLock(
			matchPredictionServiceRequest.getMatchPredictionId());
		prediction.addCount(matchPredictionServiceRequest.getSide());
		return prediction.getId();
	}

}
