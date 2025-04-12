package org.myteam.server.match.matchPredictionMember.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_match_prediction_member")
public class MatchPredictionMember extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private MatchPrediction matchPrediction;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	private Side side;

	@Builder
	public MatchPredictionMember(Long id, MatchPrediction matchPrediction, Member member, Side side) {
		this.id = id;
		this.matchPrediction = matchPrediction;
		this.member = member;
		this.side = side;
	}

	public static MatchPredictionMember createEntity(MatchPrediction matchPrediction, Member member, Side side) {
		return MatchPredictionMember.builder()
			.matchPrediction(matchPrediction)
			.member(member)
			.side(side)
			.build();
	}
}
