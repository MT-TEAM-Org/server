package org.myteam.server.board.repository;

import static org.myteam.server.board.domain.QBoardComment.boardComment;
import static org.myteam.server.board.domain.QBoardReply.boardReply;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.myteam.server.board.dto.reponse.BoardReplyResponse;
import org.myteam.server.board.service.BoardCommentRecommendReadService;
import org.myteam.server.board.service.BoardReplyRecommendReadService;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.member.repository.MemberRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardCommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;

    private final BoardCommentRecommendReadService boardCommentRecommendReadService;
    private final BoardReplyRecommendReadService boardReplyRecommendReadService;

    public List<BoardCommentResponse> getBoardCommentList(Long boardId, BoardOrderType orderType,
                                                          CustomUserDetails userDetails) {
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
                        boardComment.lastModifiedDate,
                        ExpressionUtils.as(Expressions.constant(false), "isRecommended")
                ))
                .from(boardComment)
                .where(isBoardEqualTo(boardId))
                .orderBy(isOrderByEqualToOrderType(orderType))
                .fetch();

        list.forEach(comment -> {
            boolean isRecommended = false;

            if (userDetails != null) {
                UUID loginUser = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
                isRecommended = boardCommentRecommendReadService.isRecommended(comment.getBoardCommentId(), loginUser);
            }
            comment.setRecommended(isRecommended);
            comment.setBoardReplyList(getRepliesForComments(comment.getBoardCommentId(), userDetails));
        });

        return list;
    }

    public List<BoardReplyResponse> getRepliesForComments(Long boardCommentId, CustomUserDetails userDetails) {
        List<BoardReplyResponse> replies = queryFactory
                .select(Projections.fields(BoardReplyResponse.class,
                        boardReply.boardComment.id.as("boardCommentId"),
                        boardReply.id.as("boardReplyId"),
                        boardReply.createdIp,
                        boardReply.member.publicId,
                        boardReply.member.nickname,
                        boardReply.imageUrl,
                        boardReply.comment,
                        boardReply.recommendCount,
                        boardReply.mentionedMember.publicId.as("mentionedPublicId"),
                        boardReply.mentionedMember.nickname.as("mentionedNickname"),
                        boardReply.createDate,
                        boardReply.lastModifiedDate
                ))
                .from(boardReply)
                .leftJoin(boardReply.mentionedMember)
                .where(isBoardCommentEqualTo(boardCommentId))
                .orderBy(boardReply.createDate.desc())
                .fetch();

        replies.forEach(reply -> {
            boolean isRecommended = false;

            if (userDetails != null) {
                UUID loginUser = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
                isRecommended = boardReplyRecommendReadService.isRecommended(reply.getBoardReplyId(), loginUser);
            }
            reply.setRecommended(isRecommended);
        });

        return replies;
    }

    private BooleanExpression isBoardCommentEqualTo(Long boardCommentId) {
        return boardReply.boardComment.id.eq(boardCommentId);
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
