package org.myteam.server.match.matchPredictionMember.dto.service.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchPredictionMemberDeleteResponse {

	private Long matchPredictionId;
	private UUID memberId;

	@Builder
	public MatchPredictionMemberDeleteResponse(Long matchPredictionId, UUID memberId) {
		this.matchPredictionId = matchPredictionId;
		this.memberId = memberId;
	}

	public static MatchPredictionMemberDeleteResponse createResponse(Long matchPredictionId, UUID memberId) {
		return MatchPredictionMemberDeleteResponse.builder()
			.matchPredictionId(matchPredictionId)
			.memberId(memberId)
			.build();
	}
}
