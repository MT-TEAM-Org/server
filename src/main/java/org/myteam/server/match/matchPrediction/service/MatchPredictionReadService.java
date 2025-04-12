package org.myteam.server.match.matchPrediction.service;

import java.util.UUID;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.matchPrediction.dto.service.response.MatchPredictionResponse;
import org.myteam.server.match.matchPrediction.repository.MatchPredictionLockRepository;
import org.myteam.server.match.matchPrediction.repository.MatchPredictionRepository;
import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.match.matchPredictionMember.service.MatchPredictionMemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchPredictionReadService {

	private final MatchPredictionRepository matchPredictionRepository;
	private final MatchPredictionLockRepository matchPredictionLockRepository;
	private final MatchPredictionMemberReadService matchPredictionMemberReadService;
	private final SecurityReadService securityReadService;

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
		UUID publicId = securityReadService.getAuthenticatedPublicId();

		MatchPrediction matchPrediction = findByMatchId(id);

		MatchPredictionMember matchPredictionMember = matchPredictionMemberReadService.confirmPredictionMember(
			matchPrediction.getId(), publicId);

		return MatchPredictionResponse.createResponse(
			matchPrediction,
			isRecommendYn(matchPredictionMember, publicId),
			confirmSide(matchPredictionMember)
		);
	}

	private boolean isRecommendYn(MatchPredictionMember matchPredictionMember, UUID publicId) {
		return publicId != null && matchPredictionMember != null;
	}

	private Side confirmSide(MatchPredictionMember matchPredictionMember) {
		if (matchPredictionMember == null) {
			return null;
		}
		return matchPredictionMember.getSide();
	}

}
