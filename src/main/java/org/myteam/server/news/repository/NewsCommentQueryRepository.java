package org.myteam.server.news.repository;

import static java.util.Optional.*;
import static org.myteam.server.news.domain.QNewsComment.*;

import java.util.List;

import org.myteam.server.news.dto.repository.NewsCommentDto;
import org.myteam.server.news.dto.repository.NewsCommentMemberDto;
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
public class NewsCommentQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<NewsCommentDto> getNewsCommentList(Long newsId, Pageable pageable) {
		List<NewsCommentDto> content = queryFactory
			.select(Projections.constructor(NewsCommentDto.class,
				newsComment.id,
				newsComment.news.id,
				Projections.constructor(NewsCommentMemberDto.class,
					newsComment.member.id,
					newsComment.member.nickname
				),
				newsComment.comment,
				newsComment.ip
			))
			.from(newsComment)
			.where(
				isNewsEqualTo(newsId)
			)
			.orderBy(newsComment.createDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalNewsCount(newsId);

		return new PageImpl<>(content, pageable, total);
	}

	private long getTotalNewsCount(Long newsId) {
		return ofNullable(
			queryFactory
				.select(newsComment.count())
				.from(newsComment)
				.where(
					isNewsEqualTo(newsId)
				)
				.fetchOne()
		).orElse(0L);
	}

	private BooleanExpression isNewsEqualTo(Long newsId) {
		return newsComment.news.id.eq(newsId);
	}
}
