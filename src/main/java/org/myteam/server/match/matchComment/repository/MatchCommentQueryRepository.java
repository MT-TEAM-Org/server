package org.myteam.server.match.matchComment.repository;

import static java.util.Optional.*;
import static org.myteam.server.match.matchComment.domain.QMatchComment.*;
import static org.myteam.server.news.newsComment.domain.QNewsComment.*;

import java.util.List;

import org.myteam.server.match.matchComment.domain.QMatchComment;
import org.myteam.server.match.matchComment.dto.repository.MatchCommentDto;
import org.myteam.server.match.matchComment.dto.repository.MatchCommentMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchCommentQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<MatchCommentDto> getNewsCommentList(Long matchId, Pageable pageable) {
		List<MatchCommentDto> content = queryFactory
			.select(Projections.constructor(MatchCommentDto.class,
				matchComment.id,
				matchComment.match.id,
				Projections.constructor(MatchCommentMemberDto.class,
					matchComment.member.publicId,
					matchComment.member.nickname
				),
				matchComment.comment,
				matchComment.ip,
				matchComment.createDate
			))
			.from(matchComment)
			.where(
				isMatchEqualTo(matchId)
			)
			.orderBy(matchComment.createDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalNewsCount(matchId);

		return new PageImpl<>(content, pageable, total);
	}

	private long getTotalNewsCount(Long matchId) {
		return ofNullable(
			queryFactory
				.select(matchComment.count())
				.from(matchComment)
				.where(
					isMatchEqualTo(matchId)
				)
				.fetchOne()
		).orElse(0L);
	}

	private BooleanExpression isMatchEqualTo(Long matchId) {
		return matchComment.match.id.eq(matchId);
	}
}
