package org.myteam.server.match.matchPredictionMember.service;

import java.util.UUID;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.match.matchPredictionMember.repository.MatchPredictionMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchPredictionMemberReadService {

	private final MatchPredictionMemberRepository matchPredictionMemberRepository;

	public void confirmExistMember(Long newsId, UUID memberId) {
		matchPredictionMemberRepository.findByMatchPredictionIdAndMemberPublicId(newsId, memberId)
			.ifPresent(matchPredictionMember -> {
				throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_MATCH_PREDICTION);
			});
	}

	public MatchPredictionMember confirmPredictionMember(Long matchPredictionId, UUID memberId) {
		return matchPredictionMemberRepository.findByMatchPredictionIdAndMemberPublicId(matchPredictionId, memberId)
			.orElse(null);
	}

}
