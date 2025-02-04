package org.myteam.server.news.repository;

import static java.util.Optional.*;
import static org.myteam.server.news.domain.QNewsComment.*;
import static org.myteam.server.news.domain.QNewsReply.*;

import java.util.List;

import org.myteam.server.news.dto.repository.NewsReplyDto;
import org.myteam.server.news.dto.repository.NewsReplyMemberDto;
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
public class NewsReplyQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<NewsReplyDto> getNewsReplyList(Long newsCommentId, Pageable pageable) {
		List<NewsReplyDto> content = queryFactory
			.select(Projections.constructor(NewsReplyDto.class,
				newsReply.id,
				newsReply.newsComment.id,
				Projections.constructor(NewsReplyMemberDto.class,
					newsReply.member.id,
					newsReply.member.nickname
				),
				newsReply.comment,
				newsReply.ip,
				newsReply.createDate
			))
			.from(newsReply)
			.where(
				isNewsCommentEqualTo(newsCommentId)
			)
			.orderBy(newsReply.createDate.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalNewsCount(newsCommentId);

		return new PageImpl<>(content, pageable, total);
	}

	private long getTotalNewsCount(Long newsCommentId) {
		return ofNullable(
			queryFactory
				.select(newsReply.count())
				.from(newsReply)
				.where(
					isNewsCommentEqualTo(newsCommentId)
				)
				.fetchOne()
		).orElse(0L);
	}

	private BooleanExpression isNewsCommentEqualTo(Long newsId) {
		return newsReply.newsComment.id.eq(newsId);
	}
}
