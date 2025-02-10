package org.myteam.server.news.news.repository;

import static java.util.Optional.*;
import static org.myteam.server.news.news.domain.QNews.*;
import static org.myteam.server.news.newsCount.domain.QNewsCount.*;

import java.util.List;

import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
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

	public Page<NewsDto> getNewsList(NewsServiceRequest newsServiceRequest) {
		NewsCategory category = newsServiceRequest.getCategory();
		OrderType orderType = newsServiceRequest.getOrderType();
		String content = newsServiceRequest.getContent();
		Pageable pageable = newsServiceRequest.toPageable();

		List<NewsDto> contents = queryFactory
			.select(Projections.constructor(NewsDto.class,
				news.id,
				news.category,
				news.title,
				news.thumbImg,
				news.postDate
			))
			.from(news)
			.join(newsCount).on(newsCount.news.id.eq(news.id))
			.where(
				isCategoryEqualTo(category),
				isTitleLikeTo(content)
			)
			.orderBy(isOrderByEqualToOrderType(orderType))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalNewsCount(category, content);

		return new PageImpl<>(contents, pageable, total);
	}

	private long getTotalNewsCount(NewsCategory category, String content) {
		return ofNullable(
			queryFactory
				.select(news.count())
				.from(news)
				.where(
					isCategoryEqualTo(category),
					isTitleLikeTo(content)
				)
				.fetchOne()
		).orElse(0L);
	}

	private OrderSpecifier<?> isOrderByEqualToOrderType(OrderType orderType) {
		return switch (orderType) {
			case DATE -> news.postDate.desc();
			case VIEW -> newsCount.viewCount.desc();
			case COMMENT -> newsCount.commentCount.desc();
		};
	}

	private BooleanExpression isCategoryEqualTo(NewsCategory category) {
		return category != null ? news.category.eq(category) : null;
	}

	private BooleanExpression isTitleLikeTo(String content) {
		return content != null ? news.title.like("%"+content+"%") : null;
	}
}
