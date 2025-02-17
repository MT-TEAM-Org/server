package org.myteam.server.match.matchReply.repository;

import static java.util.Optional.*;
import static org.myteam.server.match.matchReply.domain.QMatchReply.*;

import java.util.List;

import org.myteam.server.match.matchReply.dto.repository.MatchReplyDto;
import org.myteam.server.match.matchReply.dto.repository.MatchReplyMemberDto;
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
public class MatchReplyQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<MatchReplyDto> getMatchReplyList(Long matchCommentId, Pageable pageable) {
		List<MatchReplyDto> content = queryFactory
			.select(Projections.constructor(MatchReplyDto.class,
				matchReply.id,
				matchReply.matchComment.id,
				Projections.constructor(MatchReplyMemberDto.class,
					matchReply.member.publicId,
					matchReply.member.nickname
				),
				matchReply.comment,
				matchReply.ip,
				matchReply.createDate
			))
			.from(matchReply)
			.where(
				isMatchCommentEqualTo(matchCommentId)
			)
			.orderBy(matchReply.createDate.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalMatchReplyCount(matchCommentId);

		return new PageImpl<>(content, pageable, total);
	}

	private long getTotalMatchReplyCount(Long matchCommentId) {
		return ofNullable(
			queryFactory
				.select(matchReply.count())
				.from(matchReply)
				.where(
					isMatchCommentEqualTo(matchCommentId)
				)
				.fetchOne()
		).orElse(0L);
	}

	private BooleanExpression isMatchCommentEqualTo(Long matchCommentId) {
		return matchReply.matchComment.id.eq(matchCommentId);
	}
}
