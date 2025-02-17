package org.myteam.server.match.matchPrediction.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.response.MatchPredictionResponse;
import org.myteam.server.match.matchPrediction.repository.MatchPredictionLockRepository;
import org.myteam.server.match.matchPrediction.repository.MatchPredictionRepository;
import org.myteam.server.news.newsCount.domain.NewsCount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchPredictionReadService {

	private final MatchPredictionRepository matchPredictionRepository;
	private final MatchPredictionLockRepository matchPredictionLockRepository;

	public MatchPrediction findByMatchId(Long id) {
		return matchPredictionRepository.findByMatchId(id)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.MATCH_PREDICTION_NOT_FOUNT));
	}

	public MatchPrediction findByIdLock(Long newsId) {
		return matchPredictionLockRepository.findById(newsId)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.NEWS_COUNT_NOT_FOUND));
	}

	public MatchPrediction findById(Long id) {
		return matchPredictionRepository.findById(id)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.MATCH_PREDICTION_NOT_FOUNT));
	}

	public MatchPredictionResponse findOne(Long id) {
		return MatchPredictionResponse.createResponse(findByMatchId(id));
	}

}
