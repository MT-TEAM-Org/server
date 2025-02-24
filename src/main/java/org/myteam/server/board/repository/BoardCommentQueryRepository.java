package org.myteam.server.board.repository;

import static org.myteam.server.board.domain.QBoardComment.boardComment;
import static org.myteam.server.board.domain.QBoardReply.boardReply;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    public List<BoardCommentResponse> getBoardCommentList(Long boardId, CustomUserDetails userDetails) {

        // 추천수가 높은 상위 3개 댓글 조회
        List<BoardCommentResponse> topComments = getBestCommentList(boardId);

        // 상위 3개를 제외한 나머지 댓글 조회 (최신순 정렬)
        List<BoardCommentResponse> otherComments = queryFactory
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
                .where(isBoardEqualTo(boardId)
                        .and(boardComment.id.notIn(topComments.stream()
                                .map(BoardCommentResponse::getBoardCommentId)
                                .collect(Collectors.toList())))) // 상위 3개 제외
                .orderBy(boardComment.createDate.desc(), boardComment.comment.asc()) // 최신순 정렬, 같다면 댓글 가나다순
                .fetch();

        // 최종 리스트: 추천 Top 3 + 최신순 나머지 댓글
        List<BoardCommentResponse> finalList = new ArrayList<>();
        finalList.addAll(topComments);
        finalList.addAll(otherComments);

        finalList.forEach(comment -> {
            boolean isRecommended = false;

            if (userDetails != null) {
                UUID loginUser = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
                isRecommended = boardCommentRecommendReadService.isRecommended(comment.getBoardCommentId(), loginUser);
            }
            comment.setRecommended(isRecommended);
            comment.setBoardReplyList(getRepliesForComments(comment.getBoardCommentId(), userDetails));
        });

        return finalList;
    }

    /**
     * 베스트 댓글 목록 조회
     */
    private List<BoardCommentResponse> getBestCommentList(Long boardId) {
        return queryFactory
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
                .orderBy(boardComment.recommendCount.desc(), boardComment.createDate.desc()) // 추천순 + 최신순
                .limit(3)
                .fetch();
    }

    /**
     * 대댓글 목록 조회
     */
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

    private BooleanExpression isBoardEqualTo(Long boardId) {
        return boardComment.board.id.eq(boardId);
    }
}
