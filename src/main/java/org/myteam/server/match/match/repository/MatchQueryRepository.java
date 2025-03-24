package org.myteam.server.match.match.repository;

import static org.myteam.server.match.match.domain.QMatch.*;

import java.time.LocalDateTime;
import java.util.List;

import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchQueryRepository {
	private final JPAQueryFactory queryFactory;

	public List<Match> findSchedulesBetweenDate(LocalDateTime startOfDay, LocalDateTime endTime,
		MatchCategory matchCategory) {
		return queryFactory
			.selectFrom(match)
			.where(
				isBetweenDate(startOfDay, endTime),
				isCategoryEqualTo(matchCategory)
			)
			.fetch();
	}

	private BooleanExpression isBetweenDate(LocalDateTime startOfDay, LocalDateTime endTime) {
		return match.endTime.between(startOfDay, endTime);
	}

	private BooleanExpression isCategoryEqualTo(MatchCategory matchCategory) {
		return matchCategory != MatchCategory.ALL ? match.category.eq(matchCategory) : null;
	}
}
