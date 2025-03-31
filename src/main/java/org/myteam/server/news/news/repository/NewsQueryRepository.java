package org.myteam.server.news.news.repository;

import static java.util.Optional.*;
import static org.myteam.server.comment.domain.QNewsComment.*;
import static org.myteam.server.news.news.domain.QNews.*;
import static org.myteam.server.news.newsCount.domain.QNewsCount.*;

import java.time.LocalDateTime;
import java.util.List;

import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.board.dto.reponse.CommentSearchDto;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.domain.QComment;
import org.myteam.server.comment.domain.QNewsComment;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.util.domain.TimePeriod;
import org.myteam.server.news.news.dto.repository.NewsCommentSearchDto;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<NewsDto> getNewsList(NewsServiceRequest newsServiceRequest) {
		Category category = newsServiceRequest.getCategory();
		OrderType orderType = newsServiceRequest.getOrderType();
		BoardSearchType searchType = newsServiceRequest.getSearchType();
		String search = newsServiceRequest.getSearch();
		TimePeriod timePeriod = newsServiceRequest.getTimePeriod();
		Pageable pageable = newsServiceRequest.toPageable();

		List<NewsDto> contents = queryFactory
			.select(Projections.constructor(NewsDto.class,
				news.id,
				news.category,
				news.title,
				news.thumbImg,
				news.content,
				newsCount.commentCount,
				news.postDate,
				news.id.in(getHotNewsIdList())
			))
			.from(news)
			.join(newsCount).on(newsCount.news.id.eq(news.id))
			.where(
				isCategoryEqualTo(category),
				isSearchTypeLikeTo(searchType, search),
				isPostDateAfter(timePeriod)
			)
			.orderBy(isOrderByEqualToOrderCategory(orderType))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// searchType이 COMMENT일 경우, 댓글 데이터 추가
		if (searchType == BoardSearchType.COMMENT) {
			contents.forEach(newsDto -> {
				NewsCommentSearchDto commentSearch = getNewsSearchBoardComment(newsDto.getId(), search);
				newsDto.updateNewsCommentSearchDto(commentSearch);
			});
		}

		long total = getTotalNewsCount(category, searchType, search, timePeriod);

		return new PageImpl<>(contents, pageable, total);
	}

	public Page<NewsDto> getTotalList(TimePeriod timePeriod, BoardOrderType orderType,
		BoardSearchType searchType, String search, Pageable pageable) {
		List<NewsDto> contents = queryFactory
			.select(Projections.constructor(NewsDto.class,
				news.id,
				news.category,
				news.title,
				news.thumbImg,
				news.content,
				newsCount.commentCount,
				news.postDate,
				news.id.in(getHotNewsIdList())
			))
			.from(news)
			.join(newsCount).on(newsCount.news.id.eq(news.id))
			.where(
				isSearchTypeLikeTo(searchType, search),
				isPostDateAfter(timePeriod)
			)
			.orderBy(isOrderByEqualToOrderCategory(orderType))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalNewsCount(timePeriod, searchType, search);

		if (searchType == BoardSearchType.COMMENT) {
			contents.forEach(newsDto -> {
				CommentSearchDto commentSearch = getSearchNewsComment(newsDto.getId(), search);
				newsDto.setCommentSearchList(commentSearch);
			});
		}

		return new PageImpl<>(contents, pageable, total);
	}

	private NewsCommentSearchDto getNewsSearchBoardComment(Long newsId, String search) {
		JPQLQuery<NewsCommentSearchDto> query = queryFactory
			.select(Projections.fields(NewsCommentSearchDto.class,
				newsComment.id.as("commentId"),
				newsComment.comment,
				newsComment.imageUrl
			))
			.from(newsComment)
			.where(newsComment.news.id.eq(newsId));

		// 검색어가 있을 경우 해당 검색어를 포함하는 댓글만 조회
		if (search != null && !search.isEmpty()) {
			query.where(newsComment.comment.like("%" + search + "%"));
		}

		return query.orderBy(newsComment.createDate.desc(), newsComment.comment.asc())
			.fetchFirst();
	}

	private long getTotalNewsCount(Category category, BoardSearchType searchType, String search,
		TimePeriod timePeriod) {
		return ofNullable(
			queryFactory
				.select(news.count())
				.from(news)
				.where(
					isCategoryEqualTo(category),
					isSearchTypeLikeTo(searchType, search),
					isPostDateAfter(timePeriod)
				)
				.fetchOne()
		).orElse(0L);
	}

	/**
	 * TODO: 레디스에서 조회수 읽어오는 것으로 수정
	 * 핫 뉴스 ID 목록 조회
	 */
	private List<Long> getHotNewsIdList() {
		// 전체 게시글 기준 추천순 내림차순 -> 조회수 + 댓글수 내림차순 -> 제목 오름차순 -> id 오름차순
		return queryFactory
			.select(news.id)
			.from(news)
			.join(newsCount).on(newsCount.news.id.eq(news.id))
			.orderBy(
				newsCount.recommendCount.desc(),
				newsCount.viewCount.add(newsCount.commentCount).desc(),
				news.title.asc(), news.id.asc()
			)
			.limit(10)
			.fetch();
	}

	private long getTotalNewsCount(TimePeriod timePeriod, BoardSearchType searchType, String search) {
		return ofNullable(
			queryFactory
				.select(news.countDistinct())
				.from(news)
				.where(
					isSearchTypeLikeTo(searchType, search),
					isPostDateAfter(timePeriod)
				)
				.fetchOne()
		).orElse(0L);
	}

	private BooleanExpression isSearchTypeLikeTo(BoardSearchType searchType, String search) {
		if (searchType == null) {
			return null;
		}

		return switch (searchType) {
			case TITLE -> news.title.like("%" + search + "%");
			case CONTENT -> news.content.like("%" + search + "%");
			case TITLE_CONTENT -> news.title.like("%" + search + "%")
				.or(news.content.like("%" + search + "%"));
			case NICKNAME -> null;
			case COMMENT -> JPAExpressions.selectOne()
				.from(QComment.comment1)
				.where(
					QComment.comment1.comment.like("%" + search + "%")
						.and(QComment.comment1.commentType.eq(CommentType.NEWS))
						.and(QComment.comment1.as(QNewsComment.class).news.id.eq(
							news.id))
				)
				.exists();
		};
	}

	private OrderSpecifier<?>[] isOrderByEqualToOrderCategory(OrderType orderType) {
		// default 최신순
		OrderType newsOrderType = ofNullable(orderType).orElse(OrderType.DATE);
		return switch (newsOrderType) {
			case DATE -> new OrderSpecifier<?>[] {news.postDate.desc(), news.title.asc(), news.id.desc()};
			case VIEW -> new OrderSpecifier<?>[] {newsCount.recommendCount.desc(),
				newsCount.commentCount.add(newsCount.viewCount).desc(), news.title.asc(), news.id.desc()};
			case COMMENT -> new OrderSpecifier<?>[] {newsCount.commentCount.desc(), news.title.asc(), news.id.desc()};
		};
	}

	private OrderSpecifier<?>[] isOrderByEqualToOrderCategory(BoardOrderType orderType) {
		// default 최신순
		BoardOrderType boardOrderType = ofNullable(orderType).orElse(BoardOrderType.CREATE);
		return switch (boardOrderType) {
			case CREATE -> new OrderSpecifier<?>[] {news.postDate.desc(), news.title.asc(), news.id.desc()};
			case RECOMMEND -> new OrderSpecifier<?>[] {newsCount.recommendCount.desc(),
				newsCount.commentCount.add(newsCount.viewCount).desc(), news.title.asc(), news.id.desc()};
			case COMMENT -> new OrderSpecifier<?>[] {newsCount.commentCount.desc(), news.title.asc(), news.id.desc()};
		};
	}

	private BooleanExpression isCategoryEqualTo(Category category) {
		return category != null ? news.category.eq(category) : null;
	}

	private BooleanExpression isTitleLikeTo(String content) {
		return content != null ? news.title.like("%" + content + "%") : null;
	}

	private BooleanExpression isPostDateAfter(TimePeriod timePeriod) {
		return Optional.ofNullable(timePeriod)
			.map(tp -> tp.getStartDateByTimePeriod(tp))
			.map(start -> news.postDate.after(start))
			.orElse(null);
	}

	public Long findPreviousNewsId(Long newsId, Category category) {
		return queryFactory
			.select(news.id)
			.from(news)
			.where(news.id.lt(newsId), // 현재 게시글보다 작은 ID
				news.category.eq(category))
			.orderBy(news.id.desc()) // 가장 큰 ID (즉, 이전 글)
			.limit(1)
			.fetchOne();
	}

	public Long findNextNewsId(Long newsId, Category category) {
		return queryFactory
			.select(news.id)
			.from(news)
			.where(news.id.gt(newsId), // 현재 게시글보다 큰 ID
				news.category.eq(category))
			.orderBy(news.id.asc()) // 가장 작은 ID (즉, 다음 글)
			.limit(1)
			.fetchOne();
	}

	private CommentSearchDto getSearchNewsComment(Long newsId, String search) {
		JPQLQuery<CommentSearchDto> query = queryFactory
			.select(Projections.fields(CommentSearchDto.class,
				newsComment.id.as("commentId"),
				newsComment.comment,
				newsComment.imageUrl
			))
			.from(newsComment)
			.where(
				newsComment.news.id.eq(newsId),
				newsComment.comment.like("%" + search + "%")
			);

		return query.orderBy(
			newsComment.createDate.desc(),
			newsComment.comment.asc()
		).fetchFirst();
	}
}
