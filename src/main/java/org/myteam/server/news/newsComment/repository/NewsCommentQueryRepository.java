package org.myteam.server.news.newsComment.repository;

import static java.util.Optional.*;
import static org.myteam.server.news.newsComment.domain.QNewsComment.*;
import static org.myteam.server.news.newsCommentMember.domain.QNewsCommentMember.*;
import static org.myteam.server.news.newsReply.domain.QNewsReply.*;

import java.util.List;
import java.util.UUID;

import org.myteam.server.news.newsComment.dto.repository.NewsCommentDto;
import org.myteam.server.news.newsComment.dto.repository.NewsCommentMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsCommentQueryRepository {

	private static final int BEST_COMMENT_COUNT = 3;

	private final JPAQueryFactory queryFactory;

	public Page<NewsCommentDto> getNewsCommentList(Long newsId, UUID memberId, Pageable pageable) {
		List<NewsCommentDto> content = queryFactory
			.select(Projections.constructor(NewsCommentDto.class,
				newsComment.id,
				newsComment.news.id,
				Projections.constructor(NewsCommentMemberDto.class,
					newsComment.member.publicId,
					newsComment.member.nickname
				),
				newsComment.comment,
				newsComment.imgUrl,
				newsComment.ip,
				newsComment.createDate,
				newsComment.recommendCount,
				existsNewsCommentMember(memberId)
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

	public List<NewsCommentDto> getNewsBestCommentList(Long newsId, UUID memberId) {
		return queryFactory
			.select(Projections.constructor(NewsCommentDto.class,
				newsComment.id,
				newsComment.news.id,
				Projections.constructor(NewsCommentMemberDto.class,
					newsComment.member.publicId,
					newsComment.member.nickname
				),
				newsComment.comment,
				newsComment.imgUrl,
				newsComment.ip,
				newsComment.createDate,
				newsComment.recommendCount,
				existsNewsCommentMember(memberId)
			))
			.from(newsComment)
			.where(
				isNewsEqualTo(newsId)
			)
			.orderBy(newsComment.recommendCount.desc())
			.limit(BEST_COMMENT_COUNT)
			.fetch();
	}

	public int getCommentCountByPublicId(UUID publicId) {
		return queryFactory
				.select(newsComment.count())
				.from(newsComment)
				.where(newsComment.member.publicId.eq(publicId))
				.fetchOne()
				.intValue();
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

	private BooleanExpression existsNewsCommentMember(UUID memberId) {
		return memberId == null
			? Expressions.FALSE
			: JPAExpressions.selectOne()
			.from(newsCommentMember)
			.where(newsCommentMember.newsComment.id.eq(newsComment.id)
				.and(newsCommentMember.member.publicId.eq(memberId)))
			.exists();
	}
}
