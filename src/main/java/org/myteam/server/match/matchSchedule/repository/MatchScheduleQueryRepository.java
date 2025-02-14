package org.myteam.server.match.matchSchedule.repository;

import static org.myteam.server.match.matchSchedule.domain.QMatchSchedule.*;

import java.time.LocalDateTime;
import java.util.List;

import org.myteam.server.match.matchSchedule.domain.MatchCategory;
import org.myteam.server.match.matchSchedule.domain.MatchSchedule;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchScheduleQueryRepository {
	private final JPAQueryFactory queryFactory;

	public List<MatchSchedule> findSchedulesBetweenDate(LocalDateTime startOfDay, LocalDateTime endTime,
		MatchCategory matchCategory) {
		return queryFactory
			.selectFrom(matchSchedule)
			.where(
				isBetweenDate(startOfDay, endTime),
				isCategoryEqualTo(matchCategory)
			)
			.fetch();
	}

	private BooleanExpression isBetweenDate(LocalDateTime startOfDay, LocalDateTime endTime) {
		return matchSchedule.startTime.between(startOfDay, endTime);
	}

	private BooleanExpression isCategoryEqualTo(MatchCategory matchCategory) {
		return matchCategory != null ? matchSchedule.category.eq(matchCategory) : null;
	}
}
