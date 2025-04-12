package org.myteam.server.match.matchPredictionMember.dto.service.response;

import java.util.UUID;

import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchPredictionMemberResponse {

	private Long matchPredictionMemberId;
	private Long matchPredictionId;
	private UUID memberId;

	@Builder
	public MatchPredictionMemberResponse(Long matchPredictionMemberId, Long matchPredictionId, UUID memberId) {
		this.matchPredictionMemberId = matchPredictionMemberId;
		this.matchPredictionId = matchPredictionId;
		this.memberId = memberId;
	}

	public static MatchPredictionMemberResponse createResponse(MatchPredictionMember matchPredictionMember) {
		return MatchPredictionMemberResponse.builder()
			.matchPredictionMemberId(matchPredictionMember.getId())
			.matchPredictionId(matchPredictionMember.getMatchPrediction().getId())
			.memberId(matchPredictionMember.getMember().getPublicId())
			.build();
	}
}
