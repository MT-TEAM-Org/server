package org.myteam.server.viewCountMember.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ViewType {
	BOARD("board"), NEWS("news");

	private final String value;
}
