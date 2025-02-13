package org.myteam.server.board.repository;

import static org.myteam.server.board.domain.QBoardComment.boardComment;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardCommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<BoardCommentResponse> getBoardCommentList(Long boardId, BoardOrderType orderType) {
        List<BoardCommentResponse> list = queryFactory
                .select(Projections.constructor(BoardCommentResponse.class,
                        boardComment.id,
                        boardComment.board.id,
                        boardComment.createdIp,
                        boardComment.member.publicId,
                        boardComment.member.nickname,
                        boardComment.imageUrl,
                        boardComment.comment,
                        boardComment.recommendCount,
                        boardComment.createDate,
                        boardComment.lastModifiedDate))
                .from(boardComment)
                .where(isBoardEqualTo(boardId))
                .orderBy(isOrderByEqualToOrderType(orderType))
                .fetch();
        return list;
    }

    private OrderSpecifier<?> isOrderByEqualToOrderType(BoardOrderType orderType) {
        // default 추천순
        BoardOrderType order = Optional.ofNullable(orderType).orElse(BoardOrderType.RECOMMEND);
        return switch (order) {
            case CREATE -> boardComment.createDate.desc();
            case RECOMMEND -> boardComment.recommendCount.desc();
            case COMMENT -> null;
        };
    }

    private BooleanExpression isBoardEqualTo(Long boardId) {
        return boardComment.board.id.eq(boardId);
    }
}
