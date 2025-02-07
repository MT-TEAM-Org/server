package org.myteam.server.board.repository;

import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.board.domain.QBoardCount.boardCount;
import static org.myteam.server.member.entity.QMember.member;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<BoardDto> getBoardList(BoardType boardType, CategoryType categoryType,
                                       BoardOrderType orderType,
                                       Pageable pageable) {

        JPAQuery<BoardDto> query = queryFactory
                .select(Projections.constructor(BoardDto.class,
                        board.boardType,
                        board.categoryType,
                        board.id,
                        board.title,
                        board.createdIp,
                        board.thumbnail,
                        member.publicId,
                        member.nickname,
                        boardCount.commentCount,
                        board.createdAt,
                        board.updatedAt
                ))
                .from(board)
                .join(boardCount).on(boardCount.boardId.eq(board.id))
                .join(member).on(member.eq(board.member)).fetchJoin()
                .where(isBoardTypeEqualTo(boardType), isCategoryEqualTo(categoryType))
                .orderBy(isOrderByEqualToOrderCategory(orderType));

        // 페이징 처리
        QueryResults<BoardDto> queryResults = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        long total = queryResults.getTotal();  // 총 데이터 개수

        List<BoardDto> content = queryResults.getResults();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?> isOrderByEqualToOrderCategory(BoardOrderType orderType) {
        // default 최신순
        BoardOrderType boardOrderType = Optional.ofNullable(orderType).orElse(BoardOrderType.CREATE);
        return switch (boardOrderType) {
            case CREATE -> board.createdAt.desc();
            case LIKE -> boardCount.likeCount.desc();
            case COMMENT -> boardCount.commentCount.desc();
        };
    }

    private BooleanExpression isCategoryEqualTo(CategoryType categoryType) {
        return categoryType != null ? board.categoryType.eq(categoryType) : null;
    }

    private BooleanExpression isBoardTypeEqualTo(BoardType boardType) {
        return boardType != null ? board.boardType.eq(boardType) : null;
    }
}