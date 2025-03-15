package org.myteam.server.comment.repository;

import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.member.entity.QMember.member;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.domain.*;
import org.myteam.server.comment.dto.response.CommentResponse.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final CommentRepository commentRepository;

    /**
     * 대댓글 목록 조회
     */
    public List<Comment> findRepliesWithImages(Long parentId) {
        return queryFactory
                .selectFrom(comment1)
                .where(comment1.parent.id.eq(parentId)
                        .and(comment1.imageUrl.isNotNull()))
                .fetch();
    }

    /**
     * 댓글 삭제
     */
    public int deleteReply(Long parentId) {
        // 대댓글 삭제
        long deletedCount = queryFactory
                .delete(comment1)
                .where(comment1.parent.id.eq(parentId))
                .execute();

        // 부모 댓글 삭제
        commentRepository.deleteById(parentId);

        // 삭제 갯수 반환
        return (int) deletedCount + 1;
    }

    /**
     * 댓글 목록 조회 (페이징 + 최신순)
     */
    public List<CommentSaveResponse> getCommentList(CommentType type, Long contentId, Pageable pageable) {

//        NumberExpression<Long> dynamicContentId = new CaseBuilder()
//                .when(comment1.commentType.eq("BOARD")).then(comment1.as(QBoardComment.class).board.id)
//                .when(comment1.commentType.eq("IMPROVEMENT")).then(comment1.as(QImprovementComment.class).improvement.id)
//                .when(comment1.commentType.eq("INQUIRY")).then(comment1.as(QInquiryComment.class).inquiry.id)
//                .when(comment1.commentType.eq("NEWS")).then(comment1.as(QNewsComment.class).news.id)
//                .when(comment1.commentType.eq("NOTICE")).then(comment1.as(QNoticeComment.class).notice.id)
//                .otherwise(Expressions.nullExpression());
//
//        List<CommentSaveResponse> comments = queryFactory
//                .select(Projections.constructor(CommentSaveResponse.class,
//                        comment1.id,
//                        comment1.createdIp,
//                        member.publicId,
//                        member.nickname,
//                        comment1.imageUrl,
//                        comment1.comment,
//                        comment1.recommendCount,
//                        comment1.mentionedMember.publicId,
//                        comment1.mentionedMember.nickname,
//                        comment1.createDate,
//                        comment1.lastModifiedDate,
//                        ExpressionUtils.as(Expressions.constant(false), "isRecommended") // 기본값 false
//                ))
//                .from(comment1)
//                .leftJoin(comment1.member, member) // 작성자 정보 조인
//                .leftJoin(comment1.mentionedMember, member) // 언급된 사용자 정보 조인
//                .where(
//                        dynamicContentId.eq(contentId), // ✅ 동적 contentId 매핑
//                        comment1.commentType.eq(type.name())  // ✅ `comment_type` 값으로 필터링
//                )
//                .orderBy(comment1.createDate.desc(), comment1.comment.asc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        for (CommentSaveResponse parentComment : comments) {
//            getCommentReply(parentComment);
//        }
//
//        return comments;
        return null;
    }

    public void getCommentReply(CommentSaveResponse parentComment) {
        List<CommentSaveResponse> replies = queryFactory
                .select(Projections.constructor(CommentSaveResponse.class,
                        comment1.id,
                        comment1.createdIp,
                        comment1.member.publicId,
                        comment1.member.nickname,
                        comment1.imageUrl,
                        comment1.comment,
                        comment1.recommendCount,
                        comment1.mentionedMember.publicId,
                        comment1.mentionedMember.nickname,
                        comment1.createDate,
                        comment1.lastModifiedDate,
                        ExpressionUtils.as(Expressions.constant(false), "isRecommended") // 기본값 false
                ))
                .from(comment1)
                .leftJoin(comment1.member, member)
                .leftJoin(comment1.mentionedMember, member)
                .where(
                        comment1.parent.id.eq(parentComment.getCommentId()) // 부모 댓글 기준 대댓글 조회
                )
                .orderBy(comment1.createDate.asc()) // 대댓글은 오래된 순으로 정렬
                .fetch();

        parentComment.setReplyList(replies); // 대댓글을 부모 댓글에 매핑
    }

    /**
     * 베스트 댓글 목록 조회 (추천 수 기준 정렬)
     */
    public List<CommentSaveResponse> getBestCommentList(CommentType type, Long contentId, Pageable pageable) {

//        NumberExpression<Long> dynamicContentId = new CaseBuilder()
//                .when(comment1.commentType.eq("BOARD")).then(comment1.as(QBoardComment.class).board.id)
//                .when(comment1.commentType.eq("IMPROVEMENT")).then(comment1.as(QImprovementComment.class).improvement.id)
//                .when(comment1.commentType.eq("INQUIRY")).then(comment1.as(QInquiryComment.class).inquiry.id)
//                .when(comment1.commentType.eq("NEWS")).then(comment1.as(QNewsComment.class).news.id)
//                .when(comment1.commentType.eq("NOTICE")).then(comment1.as(QNoticeComment.class).notice.id)
//                .otherwise(Expressions.nullExpression());
//
//        // ✅ 베스트 댓글 조회 (추천순 정렬)
//        List<CommentSaveResponse> bestComments = queryFactory
//                .select(Projections.constructor(CommentSaveResponse.class,
//                        comment1.id,
//                        comment1.createdIp,
//                        comment1.member.publicId,
//                        comment1.member.nickname,
//                        comment1.imageUrl,
//                        comment1.recommendCount,
//                        comment1.mentionedMember.publicId,
//                        comment1.mentionedMember.nickname,
//                        comment1.createDate,
//                        comment1.lastModifiedDate,
//                        ExpressionUtils.as(Expressions.constant(false), "isRecommended") // 기본값 false
//                ))
//                .from(comment1)
//                .leftJoin(comment1.member, member) // 작성자 정보 조인
//                .leftJoin(comment1.mentionedMember, member) // 언급된 사용자 정보 조인
//                .where(
//                        dynamicContentId.eq(contentId) // ✅ 동적 contentId 매핑
//                )
//                .orderBy(comment1.recommendCount.desc()) // ✅ 추천순 정렬
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        return bestComments;
        return null;
    }
}
