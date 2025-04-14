package org.myteam.server.match.matchPrediction.service;

import java.util.UUID;

import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.MatchPredictionServiceRequest;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.matchPredictionMember.service.MatchPredictionMemberReadService;
import org.myteam.server.match.matchPredictionMember.service.MatchPredictionMemberService;
import org.myteam.server.member.service.SecurityReadService;
import org.springdoc.core.service.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchPredictionService {

	private final MatchPredictionReadService matchPredictionReadService;
	private final MatchPredictionMemberService matchPredictionMemberService;
	private final MatchPredictionMemberReadService matchPredictionMemberReadService;
	private final SecurityReadService securityReadService;

	public Long update(MatchPredictionServiceRequest matchPredictionServiceRequest) {
		UUID memberId = securityReadService.getMember().getPublicId();

		Long matchPredictionId = matchPredictionServiceRequest.getMatchPredictionId();
		Side side = matchPredictionServiceRequest.getSide();

		matchPredictionMemberReadService.confirmExistMember(matchPredictionId, memberId);

		matchPredictionMemberService.save(matchPredictionId, side);

		MatchPrediction matchPrediction = addCount(matchPredictionId, side);

		return matchPrediction.getId();
	}

	public MatchPrediction addCount(Long matchPredictionId, Side side) {
		MatchPrediction matchPrediction = matchPredictionReadService.findByIdLock(matchPredictionId);
		matchPrediction.addCount(side);
		return matchPrediction;
	}

}
