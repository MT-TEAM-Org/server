package org.myteam.server.news;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecommendYN {
	YES("추천"),
	NO("비추천");

	private final String text;

	public static RecommendYN createRecommendYN(boolean recommendYn) {
		return recommendYn ? YES : NO;
	}
}
