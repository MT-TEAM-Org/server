package org.myteam.server.news.repository;

import static java.util.Optional.*;
import static org.myteam.server.news.domain.QNews.*;
import static org.myteam.server.news.domain.QNewsCount.*;

import java.util.List;

import org.myteam.server.news.domain.NewsCategory;
import org.myteam.server.news.dto.repository.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<NewsDto> getNewsList(NewsCategory category, OrderType orderType, Pageable pageable) {
		List<NewsDto> content = queryFactory
			.select(Projections.constructor(NewsDto.class,
				news.id,
				news.category,
				news.title,
				news.thumbImg
			))
			.from(news)
			.join(newsCount).on(newsCount.news.id.eq(news.id))
			.where(
				isCategoryEqualTo(category)
			)
			.orderBy(isOrderByEqualToCategory(orderType))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalNewsCount(category);

		return new PageImpl<>(content, pageable, total);
	}

	private long getTotalNewsCount(NewsCategory category) {
		return ofNullable(
			queryFactory
				.select(news.count())
				.from(news)
				.where(
					isCategoryEqualTo(category)
				)
				.fetchOne()
		).orElse(0L);
	}

	private OrderSpecifier<?> isOrderByEqualToCategory(OrderType orderType) {
		return switch (orderType) {
			case LIKE -> newsCount.likeCount.desc();
			case COMMENT -> newsCount.commentCount.desc();
			case VIEW -> newsCount.viewCount.desc();
		};
	}

	private BooleanExpression isCategoryEqualTo(NewsCategory category) {
		return category != null ? news.category.eq(category) : null;
	}
}
