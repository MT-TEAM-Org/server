package org.myteam.server.match.matchPrediction.domain;

import org.myteam.server.global.domain.Base;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.matchPrediction.dto.service.request.Side;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "p_match_prediction")
public class MatchPrediction extends Base {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne(fetch = FetchType.LAZY)
	private Match match;
	private int home;
	private int away;

	@Builder
	public MatchPrediction(Long id, Match match, int home, int away) {
		this.id = id;
		this.match = match;
		this.home = home;
		this.away = away;
	}

	public void addCount(Side side) {
		if (side.isHome()) {
			addHome();
			return;
		}
		addAway();
	}

	private void addHome() {
		this.home += 1;
	}

	private void addAway() {
		this.away += 1;
	}
}
