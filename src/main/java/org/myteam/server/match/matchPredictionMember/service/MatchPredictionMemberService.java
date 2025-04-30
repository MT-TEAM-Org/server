package org.myteam.server.match.matchPredictionMember.service;

import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.match.matchPrediction.service.MatchPredictionReadService;
import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.match.matchPredictionMember.dto.service.response.MatchPredictionMemberDeleteResponse;
import org.myteam.server.match.matchPredictionMember.dto.service.response.MatchPredictionMemberResponse;
import org.myteam.server.match.matchPredictionMember.repository.MatchPredictionMemberRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchPredictionMemberService {

	private final MatchPredictionMemberRepository matchPredictionMemberRepository;
	private final SecurityReadService securityReadService;
	private final MatchPredictionReadService matchPredictionReadService;

	public MatchPredictionMemberResponse save(Long matchPredictionId, Side side) {
		Member member = securityReadService.getMember();
		MatchPrediction matchPrediction = matchPredictionReadService.findById(matchPredictionId);
		return MatchPredictionMemberResponse.createResponse(
			matchPredictionMemberRepository.save(MatchPredictionMember.createEntity(matchPrediction, member, side))
		);
	}

	public MatchPredictionMemberDeleteResponse deleteByMatchPredictionIdMemberId(Long matchPredictionId) {
		Member member = securityReadService.getMember();
		MatchPrediction matchPrediction = matchPredictionReadService.findById(matchPredictionId);

		matchPredictionMemberRepository.deleteByMatchPredictionIdAndMemberPublicId(matchPrediction.getId(),
			member.getPublicId());
		return MatchPredictionMemberDeleteResponse.createResponse(matchPrediction.getId(), member.getPublicId());
	}

}
