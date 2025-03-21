package org.myteam.server.news.news.repository;

import static java.util.Optional.ofNullable;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.news.news.domain.QNews.news;
import static org.myteam.server.news.newsCount.domain.QNewsCount.newsCount;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.domain.QBoardComment;
import org.myteam.server.comment.domain.QComment;
import org.myteam.server.comment.domain.QNewsComment;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.util.domain.TimePeriod;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NewsQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<NewsDto> getNewsList(NewsServiceRequest newsServiceRequest) {
        Category category = newsServiceRequest.getCategory();
        OrderType orderType = newsServiceRequest.getOrderType();
        String content = newsServiceRequest.getContent();
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
                        news.postDate
                ))
                .from(news)
                .join(newsCount).on(newsCount.news.id.eq(news.id))
                .where(
                        isCategoryEqualTo(category),
                        isTitleLikeTo(content),
                        isPostDateAfter(timePeriod)
                )
                .orderBy(isOrderByEqualToOrderType(orderType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalNewsCount(category, content, timePeriod);

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
                        news.postDate
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

        return new PageImpl<>(contents, pageable, total);
    }

    private long getTotalNewsCount(Category category, String content, TimePeriod timePeriod) {
        return ofNullable(
                queryFactory
                        .select(news.count())
                        .from(news)
                        .where(
                                isCategoryEqualTo(category),
                                isTitleLikeTo(content),
                                isPostDateAfter(timePeriod)
                        )
                        .fetchOne()
        ).orElse(0L);
    }

    private long getTotalNewsCount(TimePeriod timePeriod, BoardSearchType searchType, String search) {
        return ofNullable(
                queryFactory
                        .select(board.countDistinct())
                        .from(board)
                        .where(
                                isSearchTypeLikeTo(searchType, search),
                                isPostDateAfter(timePeriod)
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
            default -> null;
        };
    }

    private OrderSpecifier<?>[] isOrderByEqualToOrderCategory(BoardOrderType orderType) {
        // default 최신순
        BoardOrderType boardOrderType = Optional.ofNullable(orderType).orElse(BoardOrderType.CREATE);
        return switch (boardOrderType) {
            case CREATE -> new OrderSpecifier<?>[]{news.createDate.desc(), news.title.asc(), news.id.desc()};
            case RECOMMEND -> new OrderSpecifier<?>[]{newsCount.recommendCount.desc(),
                    newsCount.commentCount.add(newsCount.viewCount).desc(), news.title.asc(), news.id.desc()};
            case COMMENT -> new OrderSpecifier<?>[]{newsCount.commentCount.desc(), news.title.asc(), news.id.desc()};
        };
    }

    private long getTotalBoardCount(TimePeriod timePeriod, BoardSearchType searchType, String search) {
        return ofNullable(
                queryFactory
                        .select(board.countDistinct())
                        .from(board)
                        .where(
                                isSearchTypeLikeTo(searchType, search),
                                isPostDateAfter(timePeriod)
                        )
                        .fetchOne()
        ).orElse(0L);
    }

    private BooleanExpression isCategoryEqualTo(Category category) {
        return category != null ? news.category.eq(category) : null;
    }

    private BooleanExpression isTitleLikeTo(String content) {
        return content != null ? news.title.like("%" + content + "%") : null;
    }

    private BooleanExpression isPostDateAfter(TimePeriod timePeriod) {
        return timePeriod != null ? news.postDate.after(timePeriod.getStartDateByTimePeriod(timePeriod)) : null;
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
}
