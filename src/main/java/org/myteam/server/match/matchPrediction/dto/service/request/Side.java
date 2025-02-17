package org.myteam.server.match.matchPrediction.dto.service.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Side {
	HOME("홈팀"),
	AWAY("어웨이팀");

	private final String text;

	public boolean isHome() {
		return this == HOME;
	}
}
